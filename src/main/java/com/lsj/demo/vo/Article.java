package com.lsj.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Article {
	private int id;
	private String regDate;
	private String updateDate;
	private int memberId;
	private int boardId;
	private int hitCount;
	private int goodReactionPoint;
	private int badReactionPoint;

	private String title;
	private String body;
	private String extra__writer;
	private String searchType;
	private String searchKeyword;
	
	private String extra__goodReactionPoint;
	private String extra__badReactionPoint;
	private String extra__sumReactionPoint;
	private boolean userCanModify;
	private boolean userCanDelete;
}