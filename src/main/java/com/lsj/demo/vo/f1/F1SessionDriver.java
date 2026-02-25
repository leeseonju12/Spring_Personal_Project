package com.lsj.demo.vo.f1;

import lombok.Data;

@Data
public class F1SessionDriver {
	private long sessionKey;
	private int driverNumber;
	private String fullName;
	private String acronym;
	private String teamName;
	private String headshotUrl;
}