package com.example.deliciousBee.model.review;

import java.util.List;

import lombok.Data;

@Data
public class ReviewUpdateForm {
	
	private Long reviewId;
	private String reviewContents;
	private Double rating;
	private String recommendItems;
	private boolean fileRemoved;

}
