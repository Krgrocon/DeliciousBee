package com.example.deliciousBee.model.member;

//이늄
//서로 연관된 상수들의 집합

public enum GenderType {
	MALE("남성"),
	FEMALE("여성");
	
	private String description; //필드선언
	// 생성자, enum의 특징: 생성자의 접근지정자가 private
	private GenderType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
}
