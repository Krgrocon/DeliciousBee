package com.example.deliciousBee.model.board;


public enum CategoryType {
	한식("KOREAN"),
	양식("WESTERN"),
	일식("JAPANESE"),
	중식("CHINESE"),
	디저트("DESSERT"),
	기타("OTHERS");
	private String description;

	private CategoryType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public static CategoryType findByDescription(String description) {
		for (CategoryType categoryType : CategoryType.values()) {
			if (categoryType.getDescription().equals(description)) {
				return categoryType;
			}
		}
		return null; // 또는 예외 처리
	}
}
