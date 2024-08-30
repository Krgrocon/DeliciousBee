package com.example.deliciousBee.service.restaurant;


import com.example.deliciousBee.dto.restaurant.RestaurantDto;
import com.example.deliciousBee.model.board.Restaurant;
import com.example.deliciousBee.model.file.RestaurantAttachedFile;
import com.example.deliciousBee.repository.RestaurantRepository;
import com.example.deliciousBee.repository.RtFileRepository;
import com.example.deliciousBee.util.RestaurantFileService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantService {


    private final RestaurantRepository restaurantRepository;
	private final RtFileRepository fileRepository;
	private final RestaurantFileService fileService;

    public void saveRestaurant(Restaurant restaurant, List<RestaurantAttachedFile> attachedFile) {
    	if(attachedFile != null) {
    		restaurantRepository.save(restaurant);
    		fileRepository.saveAll(attachedFile);
    	}
    	else {
    		log.info("else filev파파파ㅏㅍ {}", attachedFile);
    		restaurantRepository.save(restaurant);
    	}
    }

//    //수정함
//    public List<Restaurant> findAll() {
//        return restaurantRepository.findAll();
//    }

    //내가 수정함
    public Page<Restaurant> findAll(Pageable pageable) {
        return restaurantRepository.findAll(pageable);
    }


    public List<Restaurant> findRandom5Restaurants() {
        return restaurantRepository.findRandom5Restaurants();
    }

    public Restaurant findRestaurant(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id).get();
        return restaurant;
    }

//    public Restaurant findRestaurant(Long id) {
//        return restaurantRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid restaurant Id:" + id));
//    }

    @Transactional
    public void updateRestaurant(Restaurant updateRestaurant) {
        Restaurant findRestaurant = findRestaurant(updateRestaurant.getId());

        findRestaurant.setName(updateRestaurant.getName());
        findRestaurant.setAddress(updateRestaurant.getAddress());
        findRestaurant.setPhone_number(updateRestaurant.getPhone_number());
        findRestaurant.setOpening_hours(updateRestaurant.getOpening_hours());
        findRestaurant.setPrice_range(updateRestaurant.getPrice_range());
        findRestaurant.setHomepage_url(updateRestaurant.getHomepage_url());
        findRestaurant.setDescription(updateRestaurant.getDescription());
        findRestaurant.setLongitude(updateRestaurant.getLongitude());
        findRestaurant.setLatitude(updateRestaurant.getLatitude());
        findRestaurant.setUpdated_at(updateRestaurant.getUpdated_at());
        findRestaurant.setCategory(updateRestaurant.getCategory());

        restaurantRepository.save(findRestaurant);
    }

    @Transactional
    public void deleteRestaurant(Long id) {
        restaurantRepository.deleteById(id);
    }

    // 카테고리
    public List<Restaurant> findByCategory(String category) {
        return restaurantRepository.findByCategory(category);
    }

    public RestaurantAttachedFile findFileByRestaurantId(Restaurant restaurant) {
    	RestaurantAttachedFile attachedFile = fileRepository.findByRestaurant(restaurant);
    	return attachedFile;
    }
    
    public RestaurantAttachedFile findFileByRestaurantAttachedFileId(Long id) {
    	Optional<RestaurantAttachedFile> attachedFile = fileRepository.findById(id);
    	return attachedFile.orElse(null);
    }
    
    @Transactional
    public Page<Restaurant> findByNameContaining(String keyword, Pageable pageable) {
    	return restaurantRepository.findByNameContaining(keyword, pageable);
    }

    public Page<Restaurant> searchByNameOrMenuName(String keyword, Pageable pageable) {
        return restaurantRepository.searchByNameOrMenuName(keyword, pageable);
    }

    public Page<RestaurantDto> searchRestaurants(String keyword, Pageable pageable, String sortBy, Double userLatitude, Double userLongitude) {
        Page<Restaurant> restaurants;

        if (keyword == null || keyword.isEmpty()) {
            // 검색어가 없는 경우 전체 레스토랑 목록 조회
            if ("distance".equals(sortBy) && userLatitude != null && userLongitude != null) {
                restaurants = restaurantRepository.findAllSortedByDistance(userLatitude, userLongitude, pageable);
            } else {
                restaurants = restaurantRepository.findAll(pageable);
            }
        } else {
            if ("distance".equals(sortBy) && userLatitude != null && userLongitude != null) {
                restaurants = restaurantRepository.searchByNameOrMenuNameSortedByDistance(keyword, userLatitude, userLongitude, pageable);
            } else {
                restaurants = restaurantRepository.searchByNameOrMenuName(keyword, pageable);
            }
        }

        // DTO로 변환
        return restaurants.map(restaurant -> {
            RestaurantDto dto = new RestaurantDto(restaurant); // RestaurantDto가 이미지 리스트를 처리
            if (userLatitude != null && userLongitude != null) {
                double distance = calculateDistance(userLatitude, userLongitude, restaurant.getLatitude(), restaurant.getLongitude());
                dto.setDistance(distance);
            }
            return dto;
        });
    }


    private Double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // 지구의 반지름 (단위: km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanceKm = R * c; // 거리 (단위: km)

        return distanceKm * 1000; // 거리 (단위: m)
    }

}