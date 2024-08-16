package com.example.deliciousBee.model.review;

import java.time.LocalDate;
import java.util.List;

import com.example.deliciousBee.model.board.Restaurant;
import com.example.deliciousBee.model.file.AttachedFile;
import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.model.report.Report;
import com.example.deliciousBee.model.report.ReportReason;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
	
	@Column(name = "report_count")
    private Integer reportCount;

    @Column(name = "report_reason")
    @Enumerated(EnumType.STRING)
    private ReportReason reportReason;
	
	@OneToMany(mappedBy = "review")
	private List<AttachedFile> attachedFile;
	
	@ManyToOne
	@JoinColumn(name = "bee_member_id")
	private BeeMember beeMember;
	
	@ManyToOne
	@JoinColumn(name = "restaurant_id")
	private Restaurant restaurant;

	@OneToMany(mappedBy = "review")
	private List<Report> reports;

}
