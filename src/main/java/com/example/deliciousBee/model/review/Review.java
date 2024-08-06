package com.example.deliciousBee.model.review;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "username", nullable = false)
	private String userName;
	
	@Column(name = "review_contents", nullable = false)
	private String reviewContents;
	
	@Column(name = "rating", nullable = false)
	private Double rating;
	
	@Column(name = "recommend_items")
	private String recommendItems;
	
	@Column(name = "visit_date", nullable = false)
	private LocalDate visitDate;
	
	@Column(name = "create_date", nullable = false)
	private LocalDate createDate;
	
	@Column(name = "like_count", nullable = false)
	private Integer likeCount;
	
	@Column(name = "dislike_count", nullable = false)
	private Integer dislikeCount;
	
	@OneToMany(mappedBy = "review")
	private List<AttachedFile> attachedFile;
	
	

}
