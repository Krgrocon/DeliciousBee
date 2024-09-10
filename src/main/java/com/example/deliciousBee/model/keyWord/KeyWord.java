package com.example.deliciousBee.model.keyWord;

import jakarta.persistence.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class KeyWord {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// 키워드 카테고리(방문목적, 분위기, 편의시설)
	@Enumerated(EnumType.STRING)
	private KeywordCategory keywordcategory;
	
	// 키워드 이름
	private String keywordName;
	
	// 키워드 생성 필드
	public KeyWord(KeywordCategory keywordcategory, String keywordName) {
		super();
		this.keywordcategory = keywordcategory;
		this.keywordName = keywordName;
	}
}


