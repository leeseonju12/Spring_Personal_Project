package com.lsj.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lsj.demo.service.ArticleService;
import com.lsj.demo.service.BoardService;
import com.lsj.demo.vo.Article;
import com.lsj.demo.vo.Board;
import com.lsj.demo.vo.ResultData;
import com.lsj.demo.vo.api.ArticleWriteRequest;

@RestController
@RequestMapping("/api/v1/articles")
public class APIv1ArticleController {

	private final ArticleService articleService;
	private final BoardService boardService;

	public APIv1ArticleController(ArticleService articleService, BoardService boardService) {
		this.articleService = articleService;
		this.boardService = boardService;
	}

	@GetMapping
	public ResultData<Map<String, Object>> getArticles(@RequestParam(defaultValue = "1") int boardId,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(defaultValue = "title") String searchKeywordTypeCode,
			@RequestParam(defaultValue = "") String searchKeyword) {
		Board board = boardService.getBoardById(boardId);

		if (board == null) {
			return ResultData.from("F-1", "존재하지 않는 게시판입니다.");
		}

		int itemsInAPage = Math.max(1, Math.min(pageSize, 100));
		int safePage = Math.max(page, 1);
		int articlesCount = articleService.getArticlesCount(boardId, searchKeywordTypeCode, searchKeyword);
		int pagesCount = (int) Math.ceil(articlesCount / (double) itemsInAPage);

		List<Article> articles = articleService.getForPrintArticles(boardId, itemsInAPage, safePage,
				searchKeywordTypeCode, searchKeyword);

		Map<String, Object> payload = new HashMap<>();
		payload.put("items", articles);
		payload.put("page", safePage);
		payload.put("pageSize", itemsInAPage);
		payload.put("itemsCount", articlesCount);
		payload.put("pagesCount", pagesCount);
		payload.put("board", board);

		return ResultData.from("S-1", "게시글 목록을 불러왔습니다.", "articles", payload);
	}

	@GetMapping("/{id}")
	public ResultData<Article> getArticle(@PathVariable int id) {
		Article article = articleService.getForPrintArticle(0, id);

		if (article == null) {
			return ResultData.from("F-1", "게시글이 존재하지 않습니다.");
		}

		return ResultData.from("S-1", "게시글을 불러왔습니다.", "article", article);
	}

	@PostMapping
	public ResultData<Article> createArticle(@RequestBody ArticleWriteRequest request) {
		ResultData<Article> validationRd = validateWriteRequest(request);
		if (validationRd != null) {
			return validationRd;
		}

		Board board = boardService.getBoardById(request.boardId());

		if (board == null) {
			return ResultData.from("F-1", "존재하지 않는 게시판입니다.");
		}

		ResultData writeRd = articleService.writeArticle(String.valueOf(request.boardId()), request.memberId(),
				request.title(), request.body());

		int createdId = (int) writeRd.getData1();
		Article created = articleService.getArticleById(createdId);

		return ResultData.from("S-1", "게시글이 작성되었습니다.", "article", created);
	}

	@PutMapping("/{id}")
	public ResultData<Article> modifyArticle(@PathVariable int id, @RequestBody ArticleWriteRequest request) {
		ResultData<Article> validationRd = validateWriteRequest(request);
		if (validationRd != null) {
			return validationRd;
		}

		Article article = articleService.getArticleById(id);

		if (article == null) {
			return ResultData.from("F-1", "게시글이 존재하지 않습니다.");
		}

		ResultData userCanModifyRd = articleService.userCanModify(request.memberId(), article);
		if (userCanModifyRd.isFail()) {
			return ResultData.from(userCanModifyRd.getResultCode(), userCanModifyRd.getMsg());
		}

		articleService.modifyArticle(id, request.title(), request.body());
		Article modified = articleService.getArticleById(id);

		return ResultData.from("S-1", "게시글이 수정되었습니다.", "article", modified);
	}

	@DeleteMapping("/{id}")
	public ResultData<Integer> deleteArticle(@PathVariable int id, @RequestParam int memberId) {
		Article article = articleService.getArticleById(id);

		if (article == null) {
			return ResultData.from("F-1", "게시글이 존재하지 않습니다.");
		}

		ResultData userCanDeleteRd = articleService.userCanDelete(memberId, article);
		if (userCanDeleteRd.isFail()) {
			return ResultData.from(userCanDeleteRd.getResultCode(), userCanDeleteRd.getMsg());
		}

		articleService.deleteArticle(id);
		return ResultData.from("S-1", "게시글이 삭제되었습니다.", "id", id);
	}

	private ResultData<Article> validateWriteRequest(ArticleWriteRequest request) {
		if (request.title() == null || request.title().trim().isEmpty()) {
			return ResultData.from("F-2", "제목을 입력해주세요.");
		}

		if (request.body() == null || request.body().trim().isEmpty()) {
			return ResultData.from("F-3", "본문을 입력해주세요.");
		}

		if (request.memberId() <= 0) {
			return ResultData.from("F-4", "유효한 memberId가 필요합니다.");
		}

		if (request.boardId() <= 0) {
			return ResultData.from("F-5", "유효한 boardId가 필요합니다.");
		}

		return null;
	}
}
