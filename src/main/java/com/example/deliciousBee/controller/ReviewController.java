package com.example.deliciousBee.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.deliciousBee.model.review.AttachedFile;
import com.example.deliciousBee.model.review.Review;
import com.example.deliciousBee.model.review.ReviewConverter;
import com.example.deliciousBee.model.review.ReviewLikeForm;
import com.example.deliciousBee.model.review.ReviewWriteForm;
import com.example.deliciousBee.service.ReviewService;
import com.example.deliciousBee.util.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/review")
public class ReviewController {

	private String uploadPath = "C:\\upload\\";
	private final ReviewService reviewService;
	private final FileService fileService;

	@GetMapping("allreview")
	public String allReview(Model model) {
		List<Review> allReview = reviewService.getAllReviewWithFiles();
		model.addAttribute("allReview", allReview);
		model.addAttribute("uploadPath", uploadPath);
		return "review/allreview";
	}

	@GetMapping("write")
	public String writeReview(Model model) {
		model.addAttribute("writeform", new ReviewWriteForm());
		return "review/write";
	}

	@PostMapping("write")
	public String postWriteReview(@Validated @ModelAttribute("writeForm") ReviewWriteForm reviewWriteForm,
			BindingResult result, @RequestParam(name = "file", required = false) MultipartFile file) {

		if (result.hasErrors()) {
			return "redirect:/";
		}

		// ReviewWriteForm -> Review 변환작업
		Review review = ReviewConverter.reviewWriteFormToReview(reviewWriteForm);
		// 파일 유호성 체크 및 저장
		if (!file.isEmpty()) {
			AttachedFile attachedFile = fileService.saveFile(file);
			attachedFile.setReview(review);
			reviewService.saveReview(review, attachedFile);
			return "redirect:/";
		}
		reviewService.saveReview(review, null);
		log.info("review:{}", review);
		return "redirect:/";

	}

	// 이미지 출력을 위한 매서드
	@GetMapping("/display")
	public ResponseEntity<Resource> display(@RequestParam("filename") String filename) {
		
		String folder = "";
		Resource resource = new FileSystemResource(uploadPath + folder + filename);
		if (!resource.exists()) {
			return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
		}
		HttpHeaders header = new HttpHeaders();
		Path filePath = null;
		try {
			filePath = Paths.get(uploadPath + folder + filename);
	   		header.add("Content-type", Files.probeContentType(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
	}
	
	// 리뷰 좋아요 처리
	@PostMapping("/{reviewId}")
	@ResponseBody
	public ResponseEntity<Map<Object, Object>> likeReview(@RequestBody ReviewLikeForm reviewLikeForm){
		Review review = ReviewConverter.reviewLikeFormToReview(reviewLikeForm);
		int likeCount = reviewService.likeReview(review.getId());
		
		Map<Object, Object> reponse = new HashMap<>();
		boolean success = true;
		reponse.put("success", success);
		reponse.put("likeCount", likeCount);
        return ResponseEntity.ok(reponse);
	}	
}