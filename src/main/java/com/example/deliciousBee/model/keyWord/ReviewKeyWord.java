package com.example.deliciousBee.model.keyWord;

import com.example.deliciousBee.model.review.Review;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ReviewKeyWord {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;
	
	@ManyToOne
    @JoinColumn(name = "keyWord_id")
	private KeyWord keyWord;
	
	@Column(name = "custom_keyword_name")
    private String customKeyWordName;

	public ReviewKeyWord(Review review, KeyWord keyWord, String customKeyWordName) {
		super();
		this.review = review;
		this.keyWord = keyWord;
		this.customKeyWordName = customKeyWordName;
	}
	
	

}
