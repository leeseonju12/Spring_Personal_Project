package com.lsj.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lsj.demo.repository.F1Repository;
import com.lsj.demo.vo.f1.F1DriverChampionshipRow;
import com.lsj.demo.vo.f1.F1RaceSession;

@Service
public class F1ChampionshipService {

	private final F1Repository f1Repository;

	public F1ChampionshipService(F1Repository f1Repository) {
		this.f1Repository = f1Repository;
	}

	public List<Integer> getSeasonYears() {
		return f1Repository.getSeasonYears();
	}

	public List<F1RaceSession> getRaceSessionsByYear(int year) {
		return f1Repository.getRaceSessionsByYear(year);
	}

	public List<F1DriverChampionshipRow> getDriverChampionshipByYear(int year) {
		F1RaceSession lastRace = f1Repository.getLastRaceSessionByYear(year);
		if (lastRace == null) {
			return List.of();
		}
		return f1Repository.getDriverChampionshipRowsBySessionKey(lastRace.getSessionKey());
	}
}