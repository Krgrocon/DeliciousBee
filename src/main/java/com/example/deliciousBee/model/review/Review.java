package com.example.deliciousBee.model.review;

import java.time.LocalDate;
import java.util.List;

import com.example.deliciousBee.model.board.Restaurant;
import com.example.deliciousBee.model.file.AttachedFile;
import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.model.menu.ReviewMenu;
import com.example.deliciousBee.model.report.ReportReason;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"attachedFile", "beeMember", "restaurant"})
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
	
	@Column(name = "tasting_rating", nullable = false)
	private Double tasteRating;
	
	@Column(name = "price_rating", nullable = false)
	private Double priceRating;
	
	@Column(name = "kind_rating", nullable = false)
	private Double kindRating;
	
	@Column(name = "recommend_items")
	private String recommendItems;
	
	@Column(name = "visit_date", nullable = false)
	private LocalDate visitDate;
	
	@Column(name = "create_date", nullable = false)
	private LocalDate createDate;
	
	@Column(name = "like_count", nullable = false)
	private Integer likeCount;
	
	@Column(name = "report_count")
    private Integer reportCount;

    @Column(name = "report_reason")
    @Enumerated(EnumType.STRING)
    private ReportReason reportReason;
    
    @Transient
    @JsonProperty
    private boolean canEdit;
    
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewMenu> reviewMenuList;
    
	@OneToMany(mappedBy = "review", fetch = FetchType.EAGER)
	@JsonManagedReference
	private List<AttachedFile> attachedFile;
	
	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "bee_member_id")
	private BeeMember beeMember; 
	
	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "restaurant_id")
	private Restaurant restaurant;

}
