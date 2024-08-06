package com.example.deliciousBee.model.member;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

	//@Pattern: 정규식 사용
	//@Max: 최댓값 지정
	//@Min: 최솟값 지정
	//Vaild: 해당 Object의 Vaildation을 실행

@Entity //JPA가 관리
@Getter @Setter @ToString
public class BeeMember {
	
	@Id  //JPA꺼
	@Column(length = 20) // vachar 20으로 만들어달라 JPA꺼
	private String member_id;    //아이디
	
	@Column(length = 20, nullable = false) // 꼭 적어야된다
	private String password;     //패스워드
	
	@Column(length = 50, nullable = false)
	private String name;         //이름
	
	@Column(length = 10)
	@Enumerated(EnumType.STRING)     //
	private GenderType gender;       //성별 이늄으로
	
	private LocalDate birth;        //생년월일
	
	@Column(length = 100)
	private String email;        //이메일
	
	
	
}
