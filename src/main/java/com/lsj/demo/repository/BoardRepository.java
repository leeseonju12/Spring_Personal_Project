package com.lsj.demo.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.lsj.demo.vo.Board;

@Mapper
public interface BoardRepository {
	Board getBoardById(int id);
	List<Board> getBoards();
}