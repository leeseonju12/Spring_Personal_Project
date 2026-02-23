package com.lsj.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lsj.demo.repository.BoardRepository;
import com.lsj.demo.vo.Board;

@Service
public class BoardService {

	private final BoardRepository boardRepository;

	public BoardService(BoardRepository boardRepository) {
		this.boardRepository = boardRepository;
	}

	public Board getBoardById(int boardId) {
		return boardRepository.getBoardById(boardId);
	}

	public List<Board> getBoards() {
		return boardRepository.getBoards();
	}

}