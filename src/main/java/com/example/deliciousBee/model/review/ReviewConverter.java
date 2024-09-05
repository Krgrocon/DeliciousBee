package com.example.deliciousBee.model.review;

import java.time.LocalDate;

public class ReviewConverter {

	public static Review reviewWriteFormToReview(ReviewWriteForm writeForm) {
		Review review = new Review();
		review.setReviewContents(writeForm.getReviewContents());
		review.setRecommendItems(writeForm.getRecommendItems());
		review.setCreateDate(LocalDate.now());
		review.setVisitDate(writeForm.getVisitDate());
		review.setLikeCount(0);
		review.setAttachedFile(writeForm.getAttachedFile());
		
		// 평점관련
		review.setRating(writeForm.getRating());
		review.setTasteRating(writeForm.getTasteRating());
		review.setPriceRating(writeForm.getPriceRating());
		review.setKindRating(writeForm.getKindRating());
		return review;
	}
	
	// reviewUpdateForm -> review
	public static Review reviewUpdateFormToReview(ReviewUpdateForm reviewUpdateForm) {
		Review review = new Review();
		review.setId(reviewUpdateForm.getReviewId());
		review.setReviewContents(reviewUpdateForm.getReviewContents());
		review.setRecommendItems(reviewUpdateForm.getRecommendItems());
		
		review.setRating(reviewUpdateForm.getRating());
		review.setTasteRating(reviewUpdateForm.getTasteRating());
		review.setPriceRating(reviewUpdateForm.getPriceRating());
		review.setKindRating(reviewUpdateForm.getKindRating());
		return review;
	}
	
	// review -> reviewUpdateForm
	public static ReviewUpdateForm reviewToReviewUpdateForm(Review review) {
		ReviewUpdateForm reviewUpdateForm = new ReviewUpdateForm();
		reviewUpdateForm.setReviewId(review.getId());
		reviewUpdateForm.setReviewContents(review.getReviewContents());
		reviewUpdateForm.setRecommendItems(review.getRecommendItems());
		
		reviewUpdateForm.setRating(review.getRating());
		reviewUpdateForm.setTasteRating(review.getTasteRating());
		reviewUpdateForm.setPriceRating(review.getPriceRating());
		reviewUpdateForm.setKindRating(review.getKindRating());
		return reviewUpdateForm;
	}

}
