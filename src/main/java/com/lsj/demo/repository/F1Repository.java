package com.lsj.demo.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.lsj.demo.vo.f1.F1ChampionshipDriverSnapshot;
import com.lsj.demo.vo.f1.F1DriverChampionshipRow;
import com.lsj.demo.vo.f1.F1RaceSession;
import com.lsj.demo.vo.f1.F1SessionDriver;

@Mapper
public interface F1Repository {
	void upsertRaceSession(F1RaceSession raceSession);

	void upsertSessionDriver(F1SessionDriver sessionDriver);

	void upsertChampionshipDriverSnapshot(F1ChampionshipDriverSnapshot snapshot);

	List<Integer> getSeasonYears();

	List<F1RaceSession> getRaceSessionsByYear(@Param("year") int year);

	F1RaceSession getLastRaceSessionByYear(@Param("year") int year);

	Long getLatestChampionshipSessionKeyByYear(@Param("year") int year);

	List<F1DriverChampionshipRow> getDriverChampionshipRowsBySessionKey(@Param("sessionKey") long sessionKey);
}