package com.example.deliciousBee.model.review;

import java.time.LocalDate;
import java.util.List;

import com.example.deliciousBee.model.file.AttachedFile;
import com.example.deliciousBee.model.menu.ReviewMenu;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewWriteForm {
	

	@NotBlank
	private String reviewContents;
	
	@NotNull
	private Double rating;
	
	// 상세 평점 부분
	private Double tasteRating;
	private Double priceRating;
	private Double kindRating;
	
	@NotBlank
	private String recommendItems;
	
	@NotNull
	private LocalDate visitDate;
	
	private List<AttachedFile> attachedFile;
	
	// 메뉴 선택 부분
	private List<ReviewMenu> reviewMenuList;
	
	
}
