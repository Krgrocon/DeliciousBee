package com.example.deliciousBee.model.member;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class BeeLoginForm {

	@Size(min = 4, max = 20, message = "4~20자로 입력해주세요")
	private String member_id;
	
	@Size(min = 4, max = 20, message = "4~20자로 입력해주세요") 
	private String password;
}
