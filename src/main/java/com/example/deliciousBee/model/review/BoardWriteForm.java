package com.example.deliciousBee.model.review;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardWriteForm {
	private Long user_id;
	private String title;
	private String content;
	private int imaged_id;
	private String category;
	private LocalDateTime create_at;
	private LocalDateTime updated_at;
	
    public static Board toBoard(BoardWriteForm form) {
        Board board = new Board();
        board.setUser_id(form.getUser_id());
        board.setTitle(form.getTitle());
        board.setContent(form.getContent());
        board.setImaged_id(form.getImaged_id());
        board.setCategory(form.getCategory());
        board.setCreate_at(LocalDateTime.now());
        board.setUpdated_at(LocalDateTime.now());
        // Set other fields as needed
        return board;
    }
}
