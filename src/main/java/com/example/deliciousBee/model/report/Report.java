package com.example.deliciousBee.model.report;

import java.time.LocalDate;

import com.example.deliciousBee.model.board.Restaurant;
import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.model.review.Review;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

@Entity
@Data
public class Report {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "bee_member_id")
    private BeeMember beeMember;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    @Enumerated(EnumType.STRING)
    private ReportReason reason;
    
    @Column(name = "report_date")
    private LocalDate reportDate;

}
