package com.lsj.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lsj.demo.service.BoardService;
import com.lsj.demo.vo.Board;
import com.lsj.demo.vo.ResultData;

@RestController
@RequestMapping("/api/v1/boards")
public class APIv1BoardController {

	private final BoardService boardService;

	public APIv1BoardController(BoardService boardService) {
		this.boardService = boardService;
	}

	@GetMapping
	public ResultData<List<Board>> getBoards() {
		List<Board> boards = boardService.getBoards();
		return ResultData.from("S-1", "게시판 목록을 불러왔습니다.", "boards", boards);
	}
}
