package com.example.deliciousBee.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.deliciousBee.model.review.AttachedFile;
import com.example.deliciousBee.model.review.Review;
import com.example.deliciousBee.repository.FileRepository;
import com.example.deliciousBee.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
//@Transactional
public class ReviewService {
	
	@Value("${file.upload.path}")
    private String uploadPath;
	
	private final ReviewRepository reviewRepository;
	private final FileRepository fileRepository;
	
	public void saveReview(Review review, AttachedFile attachedFile) {
		// 첨부 파일이 있을 경우
		if (attachedFile != null) {
			reviewRepository.save(review);
			fileRepository.save(attachedFile);
		}
		else {
			reviewRepository.save(review);
		}
	}
	
	public List<Review> getAllReviewWithFiles() {
		List<Review> reviews = reviewRepository.findAll();
		for (Review review : reviews) {
			List<AttachedFile> files = fileRepository.findByReviewId(review.getId());
			review.setAttachedFile(files);
		}
		return reviews;
	}
	
	public int likeReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).get();
        review.setLikeCount(review.getLikeCount() + 1);
        reviewRepository.save(review);
        return review.getLikeCount();
    }
}
