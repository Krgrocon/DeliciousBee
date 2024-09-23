//package com.example.deliciousBee.config;
//
//import com.example.deliciousBee.model.board.CategoryType;
//import com.example.deliciousBee.model.board.Restaurant;
//import com.example.deliciousBee.model.board.VerificationStatus;
//import com.example.deliciousBee.repository.RestaurantRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Random;
//import java.util.stream.IntStream;
//
//@Component
//public class DataLoader {
//
//    private static final List<String> RESTAURANT_NAMES = Arrays.asList(
//            "강남면옥", "한추", "고갯마루", "봉피양", "평양면옥",
//            "우래옥", "을밀대", "피양옥", "진미평양냉면", "을지면옥",
//            "필동면옥", "정인면옥", "낙원면옥", "부원면옥", "오장동흥남집",
//            "오장동함흥냉면", "신창면옥", "동아냉면", "깃대봉냉면", "해주냉면",
//            "황해도만두집", "강서면옥", "사리원", "능라도", "배꼽집",
//            "벽제갈비", "삼원가든", "대도식당", "투뿔등심", "창고43",
//            "본앤브레드", "모퉁이우", "한와담", "경천애인", "우진가"
//    );
//
//    private static final List<String> ADDRESSES = Arrays.asList(
//            "부산 중구", "부산 서구", "부산 동구", "부산 영도구",
//            "부산 남구", "부산 동래구", "부산 해운대구", "부산 수영구",
//            "부산 사하구", "부산 강서구", "부산 금정구", "부산 연제구",
//            "부산 부산진구", "부산 사상구", "부산 북구", "부산 사하구",
//            "부산 동구", "부산 남구", "부산 서구", "부산 중구",
//            "부산 기장군", "부산 사하구", "부산 동래구", "부산 해운대구",
//            "부산 금정구", "부산 연제구", "경남 창원시", "경남 진주시",
//            "경남 통영시", "경남 거제시", "경남 김해시"
//    );
//
//    // 부산 중앙역 Exit 11의 중심 좌표
//    private static final double CENTRAL_LATITUDE = 35.1796;
//    private static final double CENTRAL_LONGITUDE = 129.0756;
//
//    @Bean
//    public CommandLineRunner loadData(RestaurantRepository restaurantRepository) {
//        return args -> {
//            Random random = new Random();
//
//            IntStream.range(0, 1000).forEach(i -> {
//                Restaurant restaurant = new Restaurant();
//                restaurant.setName(RESTAURANT_NAMES.get(random.nextInt(RESTAURANT_NAMES.size())));
//                restaurant.setDescription("맛있는 밥집 " + restaurant.getName());
//
//                // 부산 중앙역 Exit 11을 중심으로 약 ±0.05도의 범위 내에서 랜덤 위치 생성
//                double latitude = CENTRAL_LATITUDE + (random.nextDouble() - 0.5) * 0.1;
//                double longitude = CENTRAL_LONGITUDE + (random.nextDouble() - 0.5) * 0.1;
//                restaurant.setLatitude(latitude);
//                restaurant.setLongitude(longitude);
//
//                // 카테고리를 랜덤하게 설정 (예: 백반, 중식 등)
//                // CategoryType enum에 적절한 값이 있는지 확인하세요
//                restaurant.setCategories(CategoryType.백반.name());
//
//                // 주소는 부산 지역으로 설정
//                String address = ADDRESSES.get(random.nextInt(ADDRESSES.size())) + " " + (random.nextInt(100) + 1) + "번지";
//                restaurant.setAddress(address);
//
//                // 전화번호는 임의로 설정하거나 null로 유지
//                restaurant.setPhone_number(null);
//
//                // 영업시간 설정
//                restaurant.setOpening_hours("11:00 AM - 09:00 PM");
//
//                // 메뉴 이름과 가격대 설정
//                restaurant.setMenu_name(restaurant.getName() + " 대표 메뉴");
//                restaurant.setPrice_range("10,000원 - 30,000원");
//
//                // 홈페이지 URL 설정 (실제 도메인과 다를 수 있음)
//                String homepage = "http://www." + restaurant.getName().replaceAll(" ", "").toLowerCase() + ".com";
//                restaurant.setHomepage_url(homepage);
//
//                // 검증 상태를 승인됨으로 설정
//                restaurant.setVerificationStatus(VerificationStatus.APPROVED);
//
//                restaurantRepository.save(restaurant);
//            });
//
//            System.out.println("1천 개의 부산 중앙역 주변 유명 밥집 더미 데이터를 생성했습니다.");
//        };
//    }
//}
