package com.example.deliciousBee.model.review;

import java.time.LocalDate;

public class ReviewConverter {

	public static Review reviewWriteFormToReview(ReviewWriteForm writeForm) {
		Review review = new Review();
		review.setUserName(writeForm.getUserName());
		review.setReviewContents(writeForm.getReviewContents());
		review.setRating(writeForm.getRating());
		review.setRecommendItems(writeForm.getRecommendItems());
		review.setCreateDate(LocalDate.now());
		review.setVisitDate(writeForm.getVisitDate());
		review.setLikeCount(0);
		review.setDislikeCount(0);
		review.setAttachedFile(writeForm.getAttachedFile());
		return review;
	}
	
	public static Review reviewLikeFormToReview(ReviewLikeForm reviewLikeForm) {
		Review review = new Review();
		review.setId(reviewLikeForm.getReviewId());
		review.setLikeCount(reviewLikeForm.getLikeCount());
		return review;
	}

}
