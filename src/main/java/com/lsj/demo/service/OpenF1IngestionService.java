package com.lsj.demo.service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsj.demo.repository.F1Repository;
import com.lsj.demo.vo.f1.F1ChampionshipDriverSnapshot;
import com.lsj.demo.vo.f1.F1RaceSession;
import com.lsj.demo.vo.f1.F1SessionDriver;

@Service
public class OpenF1IngestionService {

	private static final DateTimeFormatter DB_DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static final int MAX_RETRY_COUNT = 5;
	private static final long SNAPSHOT_CALL_INTERVAL_MS = 2_500L;

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	private final F1Repository f1Repository;

	@Value("${app.openf1.base-url:https://api.openf1.org/v1}")
	private String openF1BaseUrl;

	public OpenF1IngestionService(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper,
			F1Repository f1Repository) {
		this.restTemplate = restTemplateBuilder.build();
		this.objectMapper = objectMapper;
		this.f1Repository = f1Repository;
	}

	public void ingestSeason(int year) {
		ingestSeasonFinalDriverChampionship(year);
	}

	public void ingestSeasonFinalDriverChampionship(int year) {
		List<F1RaceSession> raceSessions = ingestRaceSessions(year);
		if (raceSessions.isEmpty()) {
			return;
		}

		F1RaceSession lastRaceSession = raceSessions.get(raceSessions.size() - 1);
		ingestDrivers(lastRaceSession.getSessionKey());
		ingestChampionshipDrivers(lastRaceSession.getSessionKey(), lastRaceSession.getMeetingKey(), year);
	}

	public void ingestSeasonDriverChampionshipSnapshots(int year) {
		List<F1RaceSession> raceSessions = ingestRaceSessions(year);
		if (raceSessions.isEmpty()) {
			return;
		}

		F1RaceSession lastRaceSession = raceSessions.get(raceSessions.size() - 1);
		ingestDrivers(lastRaceSession.getSessionKey());

		for (int i = 0; i < raceSessions.size(); i++) {
			if (i > 0) {
				sleepQuietly(SNAPSHOT_CALL_INTERVAL_MS);
			}

			F1RaceSession raceSession = raceSessions.get(i);
			ingestChampionshipDrivers(raceSession.getSessionKey(), raceSession.getMeetingKey(), year);
		}
	}

	public List<F1RaceSession> ingestRaceSessions(int year) {
		String url = String.format("%s/sessions?year=%d&session_name=Race", openF1BaseUrl, year);
		JsonNode arrayNode = getArrayNodeWithRetry(url);

		if (arrayNode == null || !arrayNode.isArray()) {
			return List.of();
		}

		for (JsonNode item : arrayNode) {
			F1RaceSession raceSession = new F1RaceSession();
			raceSession.setSessionKey(item.path("session_key").asLong());
			raceSession.setMeetingKey(item.path("meeting_key").asLong());
			raceSession.setYear(item.path("year").asInt(year));
			raceSession.setSessionName(item.path("session_name").asText("Race"));
			raceSession.setRaceName(item.path("meeting_name").asText(""));
			raceSession.setDateStart(formatDbDateTime(item.path("date_start").asText()));
			f1Repository.upsertRaceSession(raceSession);
		}

		return f1Repository.getRaceSessionsByYear(year);
	}

	public void ingestDrivers(long sessionKey) {
		String url = String.format("%s/drivers?session_key=%d", openF1BaseUrl, sessionKey);
		JsonNode arrayNode = getArrayNodeWithRetry(url);

		if (arrayNode == null || !arrayNode.isArray()) {
			return;
		}

		for (JsonNode item : arrayNode) {
			F1SessionDriver driver = new F1SessionDriver();
			driver.setSessionKey(sessionKey);
			driver.setDriverNumber(item.path("driver_number").asInt());
			driver.setFullName(item.path("full_name").asText(""));
			driver.setAcronym(item.path("name_acronym").asText(""));
			driver.setTeamName(item.path("team_name").asText(""));
			driver.setHeadshotUrl(item.path("headshot_url").asText(""));
			f1Repository.upsertSessionDriver(driver);
		}
	}

	public void ingestChampionshipDrivers(long sessionKey, long meetingKey, int year) {
		String url = String.format("%s/championship_drivers?session_key=%d", openF1BaseUrl, sessionKey);
		JsonNode arrayNode = getArrayNodeWithRetry(url);

		if (arrayNode == null || !arrayNode.isArray()) {
			return;
		}

		for (JsonNode item : arrayNode) {
			F1ChampionshipDriverSnapshot snapshot = new F1ChampionshipDriverSnapshot();
			snapshot.setSessionKey(sessionKey);
			snapshot.setMeetingKey(meetingKey);
			snapshot.setYear(year);
			snapshot.setDriverNumber(item.path("driver_number").asInt());
			snapshot.setPointsStart(intOrNull(item, "points_start"));
			snapshot.setPointsCurrent(intOrNull(item, "points_current"));
			snapshot.setPositionStart(intOrNull(item, "position_start"));
			snapshot.setPositionCurrent(intOrNull(item, "position_current"));
			try {
				snapshot.setRawJson(objectMapper.writeValueAsString(item));
			} catch (Exception e) {
				snapshot.setRawJson(item.toString());
			}
			f1Repository.upsertChampionshipDriverSnapshot(snapshot);
		}
	}

	private Integer intOrNull(JsonNode node, String fieldName) {
		JsonNode field = node.path(fieldName);
		if (field.isMissingNode() || field.isNull()) {
			return null;
		}
		return field.asInt();
	}

	private JsonNode getArrayNodeWithRetry(String url) {
		for (int attempt = 0; attempt < MAX_RETRY_COUNT; attempt++) {
			try {
				ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
				return response.getBody();
			} catch (HttpClientErrorException.TooManyRequests e) {
				if (attempt == MAX_RETRY_COUNT - 1) {
					throw e;
				}

				long retryDelayMs = parseRetryAfterMillis(e.getResponseHeaders() != null
						? e.getResponseHeaders().getFirst("Retry-After")
						: null, attempt);
				sleepQuietly(retryDelayMs);
			} catch (RestClientException e) {
				if (attempt == MAX_RETRY_COUNT - 1) {
					throw e;
				}
				sleepQuietly(backoffMillis(attempt));
			}
		}

		throw new IllegalStateException("OpenF1 API 호출 재시도에 실패했습니다.");
	}

	private long parseRetryAfterMillis(String retryAfterHeader, int attempt) {
		if (retryAfterHeader == null || retryAfterHeader.isBlank()) {
			return backoffMillis(attempt);
		}

		try {
			long seconds = Long.parseLong(retryAfterHeader.trim());
			return Math.max(1_000L, seconds * 1_000L);
		} catch (NumberFormatException e) {
			return backoffMillis(attempt);
		}
	}

	private long backoffMillis(int attempt) {
		long baseDelay = (long) (10_000L * Math.pow(2, attempt));
		long jitter = ThreadLocalRandom.current().nextLong(300L, 1_500L);
		return baseDelay + jitter;
	}

	private void sleepQuietly(long sleepMillis) {
		try {
			Thread.sleep(sleepMillis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("OpenF1 API 대기 중 인터럽트가 발생했습니다.", e);
		}
	}

	private String formatDbDateTime(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		try {
			OffsetDateTime parsed = OffsetDateTime.parse(value);
			return parsed.format(DB_DATETIME_FORMAT);
		} catch (Exception e) {
			return null;
		}
	}
}