package com.example.deliciousBee.model.review;



import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;
    private Long phone_number;
    private String opening_hours;
    private String price_range;
    private String homepage_url;


    @Column(columnDefinition = "TEXT" , nullable = false)
    private String description;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double latitude;

    //    private Long image_id;
    @Column(nullable = false)
    private LocalDateTime create_at;

    @Column(nullable = false)
    private LocalDateTime updated_at;

    private Double average_rating;
    private Long review_count;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.create_at = now;
        this.updated_at = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated_at = LocalDateTime.now();
    }

    public Restaurant(String name, String description, Double longitude, Double latitude) {
        this.name = name;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
    }

//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true) // 맛집과 이미지의 관계 설정 (CascadeType.ALL: 맛집 삭제 시 이미지도 삭제, orphanRemoval: 이미지 삭제 시 연관 관계도 삭제)
//    @JoinColumn(name = "restaurant_id") // 외래 키 설정
//    private List<Image> images = new ArrayList<>();
//
//    // 생성 시 이미지 추가하는 메서드
//    public void addImage(Image image) {
//        this.images.add(image);
//        image.setRestaurant(this); // 양방향 연관 관계 설정
//    }
}
