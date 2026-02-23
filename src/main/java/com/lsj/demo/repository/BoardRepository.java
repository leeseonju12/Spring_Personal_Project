package com.lsj.demo.repository;

import org.apache.ibatis.annotations.Mapper;

import com.lsj.demo.vo.Article;
import com.lsj.demo.vo.Board;
import java.util.List;

@Mapper
public interface BoardRepository {

	public Board getBoardById(int id);

}