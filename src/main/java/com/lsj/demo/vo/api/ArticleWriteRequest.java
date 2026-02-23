package com.lsj.demo.vo.api;

public record ArticleWriteRequest(int memberId, int boardId, String title, String body) {
}