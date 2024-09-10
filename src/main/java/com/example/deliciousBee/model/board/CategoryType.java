package com.example.deliciousBee.model.board;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CategoryType {
    백반("한식"), 죽("한식"), 국수("한식"), 찌개("한식"), 탕("한식"), 전골("한식"),
    족발("한식"), 보쌈("한식"), 한정식("한식"), 분식("한식"), 
    초밥("일식"), 회("일식"), 돈가스("일식"), 일본식카레("일식"), 일본식면요리("일식"), 
    중식("중식"), 
    파스타("양식"), 스테이크("양식"), 
    아시안("아시안"), 
    피자("패스트푸드"), 햄버거("패스트푸드"), 핫도그("패스트푸드"), 샌드위치("패스트푸드"), 
    카페("디저트"), 디저트("디저트");

    private String mainCategory;

    private CategoryType(String mainCategory) {
        this.mainCategory = mainCategory;
    }

    public String getMainCategory() {
        return mainCategory;
    }

    // 메인 카테고리 목록을 반환하는 메서드
    public static List<String> getMainCategories() {
        return Arrays.stream(CategoryType.values())
                .map(CategoryType::getMainCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    // 특정 메인 카테고리에 속하는 서브 카테고리 목록을 반환하는 메서드
    public static List<String> getSubcategoriesByMainCategory(String mainCategory) {
        return Arrays.stream(CategoryType.values())
                .filter(category -> category.getMainCategory().equals(mainCategory))
                .map(Enum::name) // enum 값의 이름을 문자열로 변환하여 반환
                .collect(Collectors.toList());
    }
    
    @JsonCreator
    public static CategoryType fromString(String key) {
        if (key == null || key.isEmpty()) {
            return null; // 또는 기본값을 설정할 수 있음
        }
        return CategoryType.valueOf(key);
    }
}