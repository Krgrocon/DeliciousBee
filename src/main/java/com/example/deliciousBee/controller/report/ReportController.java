package com.example.deliciousBee.controller.report;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import com.example.deliciousBee.dto.report.ReportDto;
import com.example.deliciousBee.dto.report.RestaurantVerificationDto;
import com.example.deliciousBee.model.board.Restaurant;
import com.example.deliciousBee.model.board.VerificationStatus;
import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.model.report.Report;
import com.example.deliciousBee.model.review.Review;
import com.example.deliciousBee.repository.ReportRepository;
import com.example.deliciousBee.service.report.ReportService;
import com.example.deliciousBee.service.restaurant.RestaurantService;
import com.example.deliciousBee.service.review.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ReportController {


	private final RestaurantService restaurantService;
	private final ReportService reportService;
	private final ReviewService reviewService;

	@GetMapping("/admin")
	public String adminPage() {
		return "admin/adminpage"; //
	}


	// 리뷰 신고
	@PostMapping("/restaurant/rtread/report/submit/{reviewId}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> submitReport(
			@RequestBody Report report,
			@PathVariable("reviewId") Long reviewId,
			@AuthenticationPrincipal BeeMember loginMember) {

		Map<String, Object> response = new HashMap<>();
		try {
			report.setBeeMember(loginMember);
			report.setReportDate(LocalDate.now());

			// 신고 제출 서비스 호출
			boolean success = reportService.sendReport(reviewId, report);
			response.put("success", success);
			if (success) {
				response.put("message", "신고가 성공적으로 제출되었습니다.");
			} else {
				response.put("message", "신고 제출에 실패하였습니다.");
			}

		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "서버 오류가 발생했습니다.");
			log.error("Report submission error", e);
		}
		return ResponseEntity.ok(response);
	}

	@GetMapping("/admin/reports/all")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllReports() {
		Map<String, Object> response = new HashMap<>();
		try {
			List<ReportDto> reportDtos = reportService.getAllReportDtos(); // DTO 사용
			response.put("reports", reportDtos);
			response.put("success", true);
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "서버 오류가 발생했습니다.");
			log.error("Report getting error", e);
		}
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/admin/report/{reportId}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteReport(@PathVariable("reportId") Long reportId) {
		Map<String, Object> response = new HashMap<>();
		try {
			Optional<Report> reportOptional = reportService.getReportById(reportId);
			if (reportOptional.isPresent()) {
				Report report = reportOptional.get();
				Long reviewId = report.getReview().getId(); // Report 엔티티에서 reviewId 가져오기
				reportService.deleteReport(reportId);
				reviewService.deleteReview(reviewId);
				response.put("success", true);
				response.put("message", "리포트와 관련된 리뷰가 성공적으로 삭제되었습니다.");
			} else {
				response.put("success", false);
				response.put("message", "Report not found");
			}
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "서버 오류가 발생했습니다.");
			log.error("Report deleting error", e);
		}
		return ResponseEntity.ok(response);
	}

	// 리뷰 삭제 엔드포인트 추가
	@DeleteMapping("/admin/reviews/{reviewId}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteReview(@PathVariable Long reviewId) {
		Map<String, Object> response = new HashMap<>();
		try {
			reportService.deleteReview(reviewId); // 리뷰와 관련된 리포트 삭제
			response.put("success", true);
			response.put("message", "리뷰와 관련된 모든 리포트가 성공적으로 삭제되었습니다.");
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "리뷰 삭제에 실패했습니다.");
			log.error("Review deletion error", e);
		}
		return ResponseEntity.ok(response);
	}

	@PostMapping("/admin/restaurant/approve/{restaurantId}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> approveRestaurant(@PathVariable("restaurantId") Long restaurantId) {
		Map<String, Object> response = new HashMap<>();
		try {
			Restaurant restaurant = restaurantService.findRestaurant(restaurantId);
			restaurantService.updateApprove(restaurant);
			response.put("success", true);
			response.put("message", "레스토랑이 성공적으로 승인되었습니다.");
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "서버 오류가 발생했습니다.");
			log.error("Restaurant approval error", e);
		}
		return ResponseEntity.ok(response);
	}

	@GetMapping("/admin/reviews/{reviewId}")
	public ResponseEntity<Review> getReviewById(@PathVariable Long reviewId) {
		Review review = reviewService.findReview(reviewId);
		if (review != null) {
			return ResponseEntity.ok(review);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/admin/restaurants/pending")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getPendingRestaurants() {
		Map<String, Object> response = new HashMap<>();
		try {
			List<RestaurantVerificationDto> pendingRestaurantDtos = restaurantService.getPendingRestaurantDtos(); // RestaurantReportDto 리스트 가져오기
			response.put("pending", pendingRestaurantDtos); // RestaurantReportDto 리스트를 "pending" 키에 담아 반환
			response.put("success", true);
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "서버 오류가 발생했습니다.");
			log.error("Pending restaurants fetching error", e);
		}
		return ResponseEntity.ok(response);
	}

	// 리포트 승인 (허가)
	@PostMapping("/admin/reports/{reportId}/approve")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> approveReport(@PathVariable Long reportId) {
		Map<String, Object> response = new HashMap<>();
		try {
			reportService.approveReport(reportId);
			response.put("success", true);
			response.put("message", "리포트가 성공적으로 승인(삭제)되었습니다.");
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "리포트 승인에 실패했습니다.");
			log.error("Report approval error", e);
		}
		return ResponseEntity.ok(response);
	}




}
