package com.example.deliciousBee.model.review;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.deliciousBee.model.file.AttachedFile;
import com.example.deliciousBee.model.menu.ReviewMenuDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewWriteForm {
	

	@NotBlank
	private String reviewContents;
	
	@NotNull
	private Integer rating;
	
	// 상세 평점 부분
	private Integer tasteRating;
	private Integer priceRating;
	private Integer kindRating;
	
	@NotNull
	private LocalDate visitDate;
	
	private List<AttachedFile> attachedFile;
	
	// 유저가 선택한 ReviewMenu
	private List<ReviewMenuDto> reviewMenuList = new ArrayList<>();
	
	// 유저가 등록한 ReviewMenu
	private String customMenuName;
	
}
