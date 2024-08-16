package com.example.deliciousBee.controller.report;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.deliciousBee.dto.report.ReportDto;
import com.example.deliciousBee.repository.ReportRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.model.report.Report;
import com.example.deliciousBee.service.report.ReportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ReportController {
	
	private final ReportService reportService;
	private final ReportRepository reportRepository;


	@GetMapping("/admin")
	public String adminPage() {
		return "admin/adminpage"; //
	}

	// 리뷰 신고
	@PostMapping("/review/allreview/report/submit/{reviewId}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> submitReport(@RequestBody Report report
			,@PathVariable("reviewId") Long reviewId
			,@SessionAttribute(name="loginMember", required=false) BeeMember loginMember) {
		
		Map<String, Object> response = new HashMap<>();
		try {
			report.setBeeMember(loginMember);
			report.setReportDate(LocalDate.now());
			
			// 신고 제출 서비스 호출, 유저는 여기서 등록
			boolean success = reportService.sendReport(reviewId, report);
			response.put("success", success);
			if(success) {
				response.put("message", "신고가 성공적으로 제출되었습니다.");
			}
			else {
				response.put("message", "신고가 컨트롤러 제출에 실패하였습니다.");
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
			reportService.deleteReport(reportId);
			response.put("success", true);
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "서버 오류가 발생했습니다.");
			log.error("Report deleting error", e);
		}
		return ResponseEntity.ok(response);
	}




}
