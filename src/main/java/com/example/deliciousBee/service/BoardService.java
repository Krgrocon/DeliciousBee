package com.example.deliciousBee.service;

import com.example.deliciousBee.model.review.Board;
import com.example.deliciousBee.repository.BoardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

	private final BoardRepository boardRepository;


	@Transactional
	public void saveBoard(Board board) {
		boardRepository.save(board);
	}
	
	public Board findBoard(Long id) {
	Board board = boardRepository.findById(id).get();
	return board;
	}
	
	public List<Board> findAll() {
		return boardRepository.findAll();
	}
}
