package com.example.deliciousBee.service.review;

import java.util.List;
import java.util.Optional;
import java.util.stream.*;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.deliciousBee.model.file.AttachedFile;
import com.example.deliciousBee.model.review.Review;
import com.example.deliciousBee.repository.FileRepository;
import com.example.deliciousBee.repository.ReportRepository;
import com.example.deliciousBee.repository.ReviewRepository;
import com.example.deliciousBee.util.FileService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewService {

	private String uploadPath = "C:\\upload\\";

	private final ReviewRepository reviewRepository;
	private final FileRepository fileRepository;
	private final FileService fileService;
	private final ReportRepository reportRepository;

	public void saveReview(Review review, AttachedFile attachedFile) {
		if (attachedFile != null) {
			reviewRepository.save(review);
			fileRepository.save(attachedFile);
		} else {
			reviewRepository.save(review);
		}
	}

	public List<Review> getReviewsByRestaurantIdWithFiles(Long restaurantId, String memberId) {
		
		List<Review> reviews = reviewRepository.findByRestaurantId(restaurantId);
		List<Long> reportedReviewIds = reportRepository.findReportedReviewId(memberId);
		
		// 신고된 리뷰를 제외한 리뷰를 필터링합니다.
		List<Review> filteredReviews = reviews.stream()
				.filter(review -> !reportedReviewIds.contains(review.getId()))
				.collect(Collectors.toList());
		
		for (Review review : filteredReviews) {
			List<AttachedFile> files = fileRepository.findByReviewId(review.getId());
			review.setAttachedFile(files);
		}
		return filteredReviews;
	}

	public int likeReview(Long reviewId) {
		Review review = reviewRepository.findById(reviewId).get();
		review.setLikeCount(review.getLikeCount() + 1);
		reviewRepository.save(review);
		return review.getLikeCount();
	}
	
	@Transactional
	public boolean deleteReview(Long reviewId) {
		try {
			fileRepository.deleteByReviewId(reviewId);
			reviewRepository.deleteById(reviewId);
			return true;
		} catch (Exception e) {
			log.error("Error deleting review with id: " + reviewId, e);
			return false;
		}
	}
	
	// 리뷰아이디 -> 리뷰
	@Transactional
	public Review findReview(Long reviewId) {
		Optional<Review> review = reviewRepository.findById(reviewId);
		return review.orElse(null);
	}

	public AttachedFile findFileByReviewId(Review review) {
		AttachedFile attachedFile = fileRepository.findByReview(review);
		return attachedFile;
	}
	
	// 리뷰 업데이트
	public void updateReview(Review updateReview, boolean fileRemoved, MultipartFile file) {
		
		Review findReview = findReview(updateReview.getId());
		findReview.setReviewContents(updateReview.getReviewContents());
		findReview.setRating(updateReview.getRating());
		findReview.setRecommendItems(updateReview.getRecommendItems());
		AttachedFile attachedFile = findFileByReviewId(findReview);
		
	    if (attachedFile != null && fileRemoved) {
	        removeFile(attachedFile);
	        attachedFile = null;
	    }
		
		if(file != null && file.getSize() > 0) {
			AttachedFile savedFile = fileService.saveFile(file);
			savedFile.setReview(findReview);
			attachedFile = savedFile;
		}
		
		if (attachedFile != null) {
	        saveReview(findReview, attachedFile);
	    }
		
		reviewRepository.save(findReview);
		
	}
	
	// 첨부파일 삭제
	private void removeFile(AttachedFile attachedFile) {
		fileRepository.deleteById(attachedFile.getAttachedFile_id());
		String fullPath = uploadPath + "/" + attachedFile.getSaved_filename();
		fileService.deleteFile(fullPath);
	}
	


	
}
