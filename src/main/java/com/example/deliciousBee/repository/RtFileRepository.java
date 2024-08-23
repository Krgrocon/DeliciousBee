package com.example.deliciousBee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.deliciousBee.model.board.Restaurant;
import com.example.deliciousBee.model.file.RestaurantAttachedFile;

public interface RtFileRepository extends JpaRepository<RestaurantAttachedFile, Long> {

	
	@Query("SELECT f FROM RestaurantAttachedFile f WHERE f.restaurant.id = :restaurantId")
	List<RestaurantAttachedFile> findFilesByRestaurantId(@Param("restaurantId") Long restaurantId);


	RestaurantAttachedFile findByRestaurant(Restaurant restaurant);

//	RestaurantAttachedFile findById(Long id);
//	
//	void saveAll(List<RestaurantAttachedFile> attachedFile);
	
}
