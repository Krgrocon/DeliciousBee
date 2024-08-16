package com.example.deliciousBee.dto.report;

import com.example.deliciousBee.model.report.ReportReason;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReviewDto {
    private Long id;
    private String userName;
    private String reviewContents;

    private Double rating;
    private String recommendItems;
    private LocalDate visitDate;
    private LocalDate createDate;
    private Integer likeCount;
    private Integer dislikeCount;
    private Integer reportCount;
    private ReportReason reportReason;


    // AttachedFile, BeeMember, Restaurant 필드는 제외합니다.
}