package com.example.deliciousBee.model.review;

import lombok.Data;

@Data
public class ReviewUpdateForm {
	
	private Long reviewId;
	private String reviewContents;
	
	private Integer rating;
	private Integer tasteRating;
	private Integer priceRating;
	private Integer kindRating;
	
	private String recommendItems;
	private boolean fileRemoved;

}
