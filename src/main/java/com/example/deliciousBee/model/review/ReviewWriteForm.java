package com.example.deliciousBee.model.review;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewWriteForm {
	
	@NotBlank
	private String userName;
	
	@NotBlank
	private String reviewContents;
	
	@NotNull
	private Double rating;
	
	@NotBlank
	private String recommendItems;
	
	@NotNull
	private LocalDate visitDate;
	
	private List<AttachedFile> attachedFile;
}
