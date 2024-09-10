//package com.example.deliciousBee.config;
//import com.example.deliciousBee.model.board.CategoryType;
//import com.example.deliciousBee.model.board.Restaurant;
//import com.example.deliciousBee.model.board.VerificationStatus;
//import com.example.deliciousBee.repository.RestaurantRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//
//import java.util.Random;
//import java.util.stream.IntStream;
//
//@Component
//public class DataLoader {
//
//    @Bean
//    public CommandLineRunner loadData(RestaurantRepository restaurantRepository) {
//        return args -> {
//            Random random = new Random();
//
//            IntStream.range(0, 10000).forEach(i -> {
//                Restaurant restaurant = new Restaurant();
//                restaurant.setName("Restaurant " + (random.nextInt(9) + 1)); // 1~9 사이의 무작위 숫자
//                restaurant.setDescription("Description for restaurant " + i);
//                restaurant.setLongitude(126.9780 + (random.nextDouble() * 0.1)); // 대략적인 서울 경도 근처
//                restaurant.setLatitude(37.5665 + (random.nextDouble() * 0.1));   // 대략적인 서울 위도 근처
//                restaurant.setCategory(CategoryType.values()[random.nextInt(CategoryType.values().length)]); // CategoryType 중 하나 선택
//                restaurant.setAddress("Address " + i);
//                restaurant.setPhone_number(10000000000L + random.nextInt(999999999)); // 무작위 전화번호 생성
//                restaurant.setOpening_hours("09:00 AM - 10:00 PM");
//                restaurant.setMenu_name("Menu " + i);
//                restaurant.setPrice_range("$" + (random.nextInt(20) + 10) + " - $" + (random.nextInt(50) + 30));
//                restaurant.setHomepage_url("http://www.restaurant" + i + ".com");
//                restaurant.setVerificationStatus(VerificationStatus.values()[random.nextInt(VerificationStatus.values().length)]); // 무작위 인증 상태
//
//                restaurantRepository.save(restaurant);
//            });
//
//            System.out.println("1만 개의 레스토랑 더미 데이터를 생성했습니다.");
//        };
//    }
//}