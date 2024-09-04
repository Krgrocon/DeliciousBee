package com.example.deliciousBee.controller.review;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.deliciousBee.model.board.Restaurant;
import com.example.deliciousBee.model.file.AttachedFile;
import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.model.review.Review;
import com.example.deliciousBee.model.review.ReviewConverter;
import com.example.deliciousBee.model.review.ReviewUpdateForm;
import com.example.deliciousBee.model.review.ReviewWriteForm;
import com.example.deliciousBee.service.restaurant.RestaurantService;
import com.example.deliciousBee.service.review.ReviewService;
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
	private final RestaurantService restaurantService;

	@PostMapping("write/{restaurant_id}")
	public String postWriteReview(@Validated @ModelAttribute("writeForm") ReviewWriteForm reviewWriteForm,
								  BindingResult result, @RequestParam(name = "file", required = false) MultipartFile[] files,
								  @AuthenticationPrincipal BeeMember loginMember, @PathVariable(name = "restaurant_id") Long restaurant_id) {

		if (result.hasErrors()) {
			return "redirect:/";
		}

		if (loginMember == null) {
			return "redirect:/login";
		}

		Review review = ReviewConverter.reviewWriteFormToReview(reviewWriteForm);
		review.setBeeMember(loginMember);
		review.setUserName(loginMember.getName());

		Restaurant restaurant = restaurantService.findRestaurant(restaurant_id);
		review.setRestaurant(restaurant);

		List<AttachedFile> attachedFiles = new ArrayList<>();
		if (files != null && files.length > 0) {
			for (MultipartFile file : files) {
				if (!file.isEmpty()) {
					AttachedFile attachedFile = fileService.saveFile(file);
					attachedFile.setReview(review);
					attachedFiles.add(attachedFile);
				}
			}
		}
		reviewService.saveReview(review, attachedFiles);
		return "redirect:/restaurant/rtread/" + restaurant_id;
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
	@PostMapping("/{reviewId}/like")
	@ResponseBody
	public ResponseEntity<Map<Object, Object>> likeReview(@PathVariable(name = "reviewId") Long reviewId,
														  @AuthenticationPrincipal BeeMember loginMember) {
		Map<Object, Object> response = new HashMap<>();
		long likeCount = reviewService.likeReview(loginMember, reviewId);
		response.put("success", true);
		response.put("likeCount", likeCount);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/{reviewId}/unlike")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> unlikeReview(@PathVariable(name = "reviewId") Long reviewId
			, @AuthenticationPrincipal BeeMember loginMember) {
		Map<String, Object> response = new HashMap<>();
		int likeCount = reviewService.unlikeReview(loginMember, reviewId);
		response.put("success", true);
		response.put("likeCount", likeCount);
		return ResponseEntity.ok(response);
	}

	// 리뷰삭제
	@PostMapping("/delete/{reviewId}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteReview(@PathVariable("reviewId") Long reviewId) {
		Map<String, Object> response = new HashMap<>();
		boolean success = reviewService.deleteReview(reviewId);
		response.put("success", success);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/update/{reviewId}")
	public String getUpdateReview(@AuthenticationPrincipal BeeMember loginMember,
								  @PathVariable("reviewId") Long reviewId, Model model) {
		Review findReview = reviewService.findReview(reviewId);
		if (findReview == null || !findReview.getBeeMember().getMember_id().equals(loginMember.getMember_id())) {
			log.info("허용 되지 않는 접근 방식입니다");
			return "redirect:/review/allreview/";
		}

		ReviewUpdateForm reviewUpdateForm = ReviewConverter.reviewToReviewUpdateForm(findReview);
		model.addAttribute("reviewUpdateForm", reviewUpdateForm);

		List<AttachedFile> attachedFile = reviewService.findFilesByReviewId(findReview.getId());
		if (attachedFile != null) {
			model.addAttribute("file", attachedFile);
		}

		return "/review/update";
	}

	// 리뷰 수정
	@PostMapping("/update")
	public String postUpdateReview(@Validated @ModelAttribute ReviewUpdateForm reviewUpdateForm, BindingResult result,
								   @RequestParam(name = "file", required = false) MultipartFile[] file) {
		Review updateReview = ReviewConverter.reviewUpdateFormToReview(reviewUpdateForm);
		reviewService.updateReview(updateReview, reviewUpdateForm.isFileRemoved(), file);
		return "redirect:/restaurant/rtread/";
	}

	// 수정시 첨부파일 삭제
	// 수정시 첨부파일 삭제
	@PostMapping("/update/remove/{attachedFileId}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteAttachedFile(@PathVariable("attachedFileId") Long attachedFileId) {
		Map<String, Object> response = new HashMap<>();
		boolean success = reviewService.deleteAttachedFile(attachedFileId);
		response.put("success", success);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/allreview/{restaurant_id}/sort/")
	@ResponseBody
	public List<Review> getSortAllReview(
			@RequestParam(required = false, defaultValue = "modifiedAt", value = "sortBy") String sortBy,
			@PathVariable("restaurant_id") Long restaurant_id, @AuthenticationPrincipal BeeMember loginMember) {
		try {
			String memberId = loginMember.getMember_id();
			log.info("정상적으로 처리가 되었습니다.");
			return reviewService.sortReview(sortBy, restaurant_id, memberId);
		} catch (Exception e) {
			log.error("서버 처리 중 오류 발생", e);
			return Collections.emptyList();
		}

	}
}