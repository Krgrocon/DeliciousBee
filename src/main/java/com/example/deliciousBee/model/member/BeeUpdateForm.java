package com.example.deliciousBee.model.member;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.deliciousBee.model.member.BeeMember;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class BeeUpdateForm {

	@Column(length = 20, nullable = false) // 꼭 적어야된다
	private String password;     //패스워드
	
	
	private LocalDate birth;        //생년월일
	
	@Column(length = 100)
	private String email;        //이메일
	
	public static BeeMember toBeeMember(BeeUpdateForm beeUpdateForm) {
		BeeMember member = new BeeMember();
		
		member.setPassword(beeUpdateForm.getPassword());
		member.setBirth(beeUpdateForm.getBirth());
		member.setEmail(beeUpdateForm.getEmail());
		
		return member;
	}
}
