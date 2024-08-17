package com.example.deliciousBee.service.review;

import java.util.ArrayList;
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

	public void saveReview(Review review, List<AttachedFile> attachedFile) {
		if (attachedFile != null) {
			reviewRepository.save(review);
			fileRepository.saveAll(attachedFile);
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
			List<AttachedFile> files = fileRepository.findFilesByReviewId(review.getId());
			review.setAttachedFile(files);
		}
		return filteredReviews;
	}

	// 좋아요 로직
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

	public List<AttachedFile> findFilesByReviewId(Long reviewId) {
		return fileRepository.findFilesByReviewId(reviewId);
	}

	// 리뷰 업데이트
	public void updateReview(Review updateReview, boolean fileRemoved, MultipartFile[] files) {

		Review findReview = findReview(updateReview.getId());
		findReview.setReviewContents(updateReview.getReviewContents());
		findReview.setRating(updateReview.getRating());
		findReview.setRecommendItems(updateReview.getRecommendItems());

		//기존 파일 가져오기
		List<AttachedFile> attachedFiles = findFilesByReviewId(findReview.getId());

		if (attachedFiles != null && fileRemoved) {
			removeFiles(attachedFiles);
			attachedFiles = null;
		}

		// 새로운 파일처리
		if(files != null && files.length > 0) {
			attachedFiles = new ArrayList<>();
			for(MultipartFile file : files) {
				if(file.isEmpty()) {
					continue;
				}
				AttachedFile savedFile = fileService.saveFile(file);
				if (savedFile != null) {
					savedFile.setReview(findReview);
					attachedFiles.add(savedFile);
				}
			}
		}

		if (attachedFiles != null && !attachedFiles.isEmpty()) {
			saveReview(findReview, attachedFiles);
		}

		reviewRepository.save(findReview);

	}

	// 첨부파일 삭제
	public void removeFiles(List<AttachedFile> attachedFiles) {
		for (AttachedFile attachedFile : attachedFiles) {
			fileRepository.deleteById(attachedFile.getAttachedFile_id());
			String fullPath = uploadPath + "/" + attachedFile.getSaved_filename();
			fileService.deleteFile(fullPath);
		}
	}

	// 수정시 첨부파일 삭제
	public boolean deleteAttachedFile(Long attachedFileId) {
		try {
			fileRepository.deleteById(attachedFileId);
			return true;
		} catch (Exception e) {
			log.error("Error deleting File with id: " + attachedFileId, e);
			return false;
		}
	}

	// 리뷰 정렬 로직
	public List<Review> sortReview(String sort,Long restaurantId, String memberId){
		log.info("*** sort:{}", sort);
		log.info("*** restaurantId:{}", restaurantId);
		log.info("*** memberId:{}", memberId);
		return switch (sort) {
			case "rating" -> reviewRepository.findAllByOrderByRatingDesc();
			case "date" -> reviewRepository.findAllByOrderByCreateDateDesc();
			default -> getReviewsByRestaurantIdWithFiles(restaurantId, memberId);
		};
	}


	
}
