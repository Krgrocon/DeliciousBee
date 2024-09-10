package com.example.deliciousBee.service.keyWord;

import java.util.*;

import org.springframework.stereotype.Service;

import com.example.deliciousBee.model.keyWord.KeyWord;
import com.example.deliciousBee.model.keyWord.KeywordCategory;
import com.example.deliciousBee.model.keyWord.ReviewKeyWord;
import com.example.deliciousBee.repository.KeyWordRepository;
import com.example.deliciousBee.repository.ReviewKeyWordRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewKeyWordService {

	private final ReviewKeyWordRepository reviewKeyWordRepository;
	private final KeyWordRepository keyWordRepository;

	// 전체 키워드 전달
	public List<KeyWord> findAllKeywords() {
		return keyWordRepository.findAll();
	}

	// 카테고리를 분류해서 전달
	public Map<KeywordCategory, List<KeyWord>> getKeywordsByCategory() {
		Map<KeywordCategory, List<KeyWord>> keywordsByCategory = new HashMap<>();
		List<KeyWord> allKeywords = keyWordRepository.findAll();

		for (KeyWord keyword : allKeywords) {
			KeywordCategory category = keyword.getKeywordcategory();
			keywordsByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(keyword);
		}

		return keywordsByCategory;
	}

	public KeyWord findById(Long keywordId) {
		return keyWordRepository.findById(keywordId).get();
	}

	public void save(ReviewKeyWord reviewKeyWord) {
		reviewKeyWordRepository.save(reviewKeyWord);

	}

}
