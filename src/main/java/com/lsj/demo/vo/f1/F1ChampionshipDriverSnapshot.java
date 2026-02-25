package com.lsj.demo.vo.f1;

import lombok.Data;

@Data
public class F1ChampionshipDriverSnapshot {
	private long sessionKey;
	private long meetingKey;
	private int year;
	private int driverNumber;
	private Integer pointsStart;
	private Integer pointsCurrent;
	private Integer positionStart;
	private Integer positionCurrent;
	private String rawJson;
}