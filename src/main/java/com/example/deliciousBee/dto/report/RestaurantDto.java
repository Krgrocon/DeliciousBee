package com.example.deliciousBee.dto.report;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RestaurantDto {
    private Long id;
    private String name;
    private String address;
    private Long phone_number;
    private String opening_hours;
    private String price_range;
    private String homepage_url;
    private String description;
    private Double longitude;
    private Double latitude;
    private LocalDateTime create_at;
    private LocalDateTime updated_at;
    private Double average_rating;
    private Long review_count;

    // Review 필드는 제외합니다.
}