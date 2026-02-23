package com.lsj.demo.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.lsj.demo.vo.Article;

@Mapper
public interface ArticleRepository {

	public int writeArticle(String boardId, int memberId, String title, String body);
	public void deleteArticle(int id);
	public void modifyArticle(int id, String title, String body);
	public Article getArticleById(int id);
	public List<Article> getArticles();
	public int getLastInsertId();
	public Article getForPrintArticle(int id);
	public List<Article> getForPrintArticles(int boardId, int limitFrom, int limitTake, String searchKeywordTypeCode,
			String searchKeyword);
	public int getArticlesCount(int boardId, String searchKeywordTypeCode, String searchKeyword);
	public int increaseHitCount(int id);
	public int getArticleHitCount(int id);
	public int increaseGoodReactionPoint(int relId);
	public int decreaseGoodReactionPoint(int relId);
	public int increaseBadReactionPoint(int relId);
	public int decreaseBadReactionPoint(int relId);
	public int getGoodRP(int relId);
	public int getBadRP(int relId);
}