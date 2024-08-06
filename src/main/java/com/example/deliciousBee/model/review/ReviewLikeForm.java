package com.example.deliciousBee.model.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewLikeForm {
	
	private Long reviewId;
	private Integer likeCount;
}
