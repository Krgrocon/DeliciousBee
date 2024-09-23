package com.example.deliciousBee.model.member;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.deliciousBee.model.member.BeeMember;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class BeeUpdateForm {


	private String member_id;

	@NotBlank //빈, null 허용안함
	private String nickname;     //닉네임


	private LocalDate birth;        //생년월일
	
	@Column(length = 100)
	private String email;        //이메일


	private NationalType national;

	private boolean fileRemoved;
	
	public static BeeMember toBeeMember(BeeUpdateForm beeUpdateForm) {
		BeeMember member = new BeeMember();
		member.setMember_id(beeUpdateForm.getMember_id()); 
		member.setNickname(beeUpdateForm.getNickname());
		member.setNational(beeUpdateForm.getNational());
		member.setEmail(beeUpdateForm.getEmail());

		return member;
	}
}
