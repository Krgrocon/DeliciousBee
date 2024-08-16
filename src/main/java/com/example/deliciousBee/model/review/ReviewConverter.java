package com.example.deliciousBee.model.review;

import java.time.LocalDate;

public class ReviewConverter {

	public static Review reviewWriteFormToReview(ReviewWriteForm writeForm) {
		Review review = new Review();
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
	
	// reviewUpdateForm -> review
	public static Review reviewUpdateFormToReview(ReviewUpdateForm reviewUpdateForm) {
		Review review = new Review();
		review.setId(reviewUpdateForm.getReviewId());
		review.setReviewContents(reviewUpdateForm.getReviewContents());
		review.setRating(reviewUpdateForm.getRating());
		review.setRecommendItems(reviewUpdateForm.getRecommendItems());
		return review;
	}
	
	// review -> reviewUpdateForm
	public static ReviewUpdateForm reviewToReviewUpdateForm(Review review) {
		ReviewUpdateForm reviewUpdateForm = new ReviewUpdateForm();
		reviewUpdateForm.setReviewId(review.getId());
		reviewUpdateForm.setReviewContents(review.getReviewContents());
		reviewUpdateForm.setRecommendItems(review.getRecommendItems());
		reviewUpdateForm.setRating(review.getRating());
		return reviewUpdateForm;
	}

}
