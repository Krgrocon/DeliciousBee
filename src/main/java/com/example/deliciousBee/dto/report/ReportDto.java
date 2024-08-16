package com.example.deliciousBee.dto.report;

import com.example.deliciousBee.model.report.ReportReason;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReportDto {
    private Long id;
    private ReportReason reason;
    private LocalDate reportDate;
    private Long reviewId; // 추가
    private String contents; // 추가
    public void setReviewId(Long reviewId) { // 추가
        this.reviewId = reviewId;
    }
    // Restaurant, BeeMember, Review 필드는 제외합니다.
}