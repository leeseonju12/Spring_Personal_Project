package com.lsj.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lsj.demo.service.F1ChampionshipService;
import com.lsj.demo.service.OpenF1IngestionService;
import com.lsj.demo.vo.ResultData;
import com.lsj.demo.vo.f1.F1DriverChampionshipRow;
import com.lsj.demo.vo.f1.F1RaceSession;

@RestController
@RequestMapping("/api/seasons")
public class APIF1SeasonController {

	private final F1ChampionshipService f1ChampionshipService;
	private final OpenF1IngestionService openF1IngestionService;

	public APIF1SeasonController(F1ChampionshipService f1ChampionshipService,
			OpenF1IngestionService openF1IngestionService) {
		this.f1ChampionshipService = f1ChampionshipService;
		this.openF1IngestionService = openF1IngestionService;
	}

	@GetMapping
	public ResultData<List<Integer>> getSeasons() {
		return ResultData.from("S-1", "시즌 목록을 조회했습니다.", "seasons", f1ChampionshipService.getSeasonYears());
	}

	@GetMapping("/{year}/races")
	public ResultData<List<F1RaceSession>> getRaceSessions(@PathVariable int year) {
		return ResultData.from("S-1", "시즌 라운드 목록을 조회했습니다.", "races", f1ChampionshipService.getRaceSessionsByYear(year));
	}

	@GetMapping("/{year}/championship/drivers")
	public ResultData<List<F1DriverChampionshipRow>> getDriverChampionship(@PathVariable int year) {
		List<F1DriverChampionshipRow> rows = f1ChampionshipService.getDriverChampionshipByYear(year);
		if (rows.isEmpty()) {
			return ResultData.from("F-1", "해당 시즌의 챔피언십 데이터가 없습니다. 먼저 수집을 실행해 주세요.");
		}
		return ResultData.from("S-1", "드라이버 챔피언십 데이터를 조회했습니다.", "driversChampionship", rows);
	}


	@GetMapping("/{year}/ingestions")
	public ResultData<String> getIngestionGuide(@PathVariable int year) {
		return ResultData.from("S-1", "해당 URL은 POST로 호출해야 실제 수집이 실행됩니다.", "year", String.valueOf(year));
	}

	@PostMapping("/{year}/ingestions")
	public ResultData<String> ingestSeasonFinalChampionship(@PathVariable int year) {
		openF1IngestionService.ingestSeasonFinalDriverChampionship(year);
		return ResultData.from("S-1", "OpenF1 시즌 최종 챔피언십 수집이 완료되었습니다.", "year", String.valueOf(year));
	}


	@GetMapping("/{year}/ingestions/snapshots")
	public ResultData<String> getSnapshotIngestionGuide(@PathVariable int year) {
		return ResultData.from("S-1", "해당 URL은 POST로 호출해야 라운드별 스냅샷 수집이 실행됩니다.", "year", String.valueOf(year));
	}

	@PostMapping("/{year}/ingestions/snapshots")
	public ResultData<String> ingestSeasonSnapshots(@PathVariable int year) {
		openF1IngestionService.ingestSeasonDriverChampionshipSnapshots(year);
		return ResultData.from("S-1", "OpenF1 시즌 라운드별 챔피언십 스냅샷 수집이 완료되었습니다.", "year", String.valueOf(year));
	}
}
