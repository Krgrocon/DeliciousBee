package com.example.deliciousBee.dto.restaurant;

import com.example.deliciousBee.model.board.CategoryType;
import com.example.deliciousBee.model.board.Restaurant;
import com.example.deliciousBee.model.file.RestaurantAttachedFile;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class RestaurantDto {
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Double distance;
    private String address;
    private String categories;
    private String mainCategory;
    private String description;
    private double average_rating;
    private List<String> imageUrls; // 이미지 URL 리스트

    // Restaurant 엔티티를 받아서 RestaurantDto를 생성하는 생성자
    public RestaurantDto(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.latitude = restaurant.getLatitude();
        this.longitude = restaurant.getLongitude();
        this.address = restaurant.getAddress();
        this.categories = restaurant.getCategories();
        this.mainCategory = restaurant.getMainCategory();
        this.description = restaurant.getDescription();
        this.average_rating = restaurant.getAverage_rating() != null ? restaurant.getAverage_rating() : 0.0;

        // attachedFile 리스트에서 이미지 URL만 추출하여 리스트로 저장
        this.imageUrls = restaurant.getAttachedFile().stream()
                .map(RestaurantAttachedFile::getSavedFilename) // 또는 실제 URL로 변환하는 메서드
                .collect(Collectors.toList());

        // distance는 계산 후 설정하므로 여기서는 초기화하지 않습니다.
    }
}
