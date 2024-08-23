package com.example.deliciousBee.model.file;

import com.example.deliciousBee.model.review.Review;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberAttachedFile {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long attachedFile_id;   //첨부파일 아이디

	@ManyToOne
	@JoinColumn(name="review_id")
	private Review review;

	private String original_filename;  //원본 파일이름
	private String saved_filename;     //저장할 파일이름
	private Long file_size;            //파일용량

	public MemberAttachedFile(String original_filename, String saved_filename, Long file_size) {
		this.original_filename = original_filename;
		this.saved_filename = saved_filename;
		this.file_size = file_size;
	}

}
