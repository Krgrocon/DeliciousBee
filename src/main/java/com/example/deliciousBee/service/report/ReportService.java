package com.example.deliciousBee.service.report;

import java.util.List;
import java.util.stream.Collectors;

import com.example.deliciousBee.dto.report.ReportDto;
import org.springframework.stereotype.Service;

import com.example.deliciousBee.model.report.Report;
import com.example.deliciousBee.model.review.Review;
import com.example.deliciousBee.repository.ReportRepository;
import com.example.deliciousBee.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {
	
	private final ReportRepository reportRepository;
	private final ReviewRepository reviewRepository;
	
	// 신고 보내는 기능
	public boolean sendReport(Long reviewId, Report report) {
		try {
			Review findReview = reviewRepository.findById(reviewId).get();
			
			// 리뷰 유무 체크
			if(findReview == null) {
				return false;
			}

			// 신고 등록
			report.setReview(findReview);
			reportRepository.save(report);
			return true;
		} catch (Exception e) {
			log.error("신고 서버 제출에 실패하였습니다.", e);
			return false;
		}
	}

	public List<ReportDto> getAllReportDtos() {
		List<Report> reports = reportRepository.findAll();
		return reports.stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	private ReportDto convertToDto(Report report) {
		ReportDto dto = new ReportDto();
		dto.setId(report.getId());
		dto.setReason(report.getReason());
		dto.setReportDate(report.getReportDate());


		if (report.getReview() != null) {
			dto.setReviewId(report.getReview().getId());
		}
		// 필요한 경우, Review, Restaurant, BeeMember 정보를 DTO에 추가합니다.
		// 예시:
		// dto.setReviewId(report.getReview().getId());
		// dto.setRestaurantName(report.getRestaurant().getName());
		// dto.setBeeMemberId(report.getBeeMember().getMember_id());

		return dto;
	}

	public void deleteReport(Long reportId) {
		reportRepository.deleteById(reportId);
	}
}
