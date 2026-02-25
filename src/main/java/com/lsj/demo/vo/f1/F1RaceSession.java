package com.lsj.demo.vo.f1;

import lombok.Data;

@Data
public class F1RaceSession {
	private long sessionKey;
	private long meetingKey;
	private int year;
	private String sessionName;
	private String raceName;
	private String dateStart;
}