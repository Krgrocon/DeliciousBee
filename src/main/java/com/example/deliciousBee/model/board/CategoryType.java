package com.example.deliciousBee.model.board;


public enum CategoryType {
	한식("KOREAN"),
	양식("WESTERN"),
	일식("JAPANESE"),
	중식("CHINESE"),
	디저트("DESSERT");
	
	private String description;
	
	private CategoryType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
}
