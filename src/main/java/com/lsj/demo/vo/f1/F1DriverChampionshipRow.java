package com.lsj.demo.vo.f1;

import lombok.Data;

@Data
public class F1DriverChampionshipRow {
	private Integer position;
	private Integer driverNumber;
	private String fullName;
	private String acronym;
	private String teamName;
	private String headshotUrl;
	private Integer points;
	private Integer pointsDeltaInRace;
	private Integer positionDeltaInRace;
}