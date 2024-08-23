package com.example.deliciousBee.model.mypage;

import java.util.List;

import com.example.deliciousBee.model.file.AttachedFile;
import com.example.deliciousBee.model.member.BeeMember;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @ToString
public class MyPage {
	
	@OneToOne
	@JoinColumn(name="member_id")
	private BeeMember beeMember;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 생성 전략 설정
	@Column(name = "myPage_id")
	private Long myPage_id;
	
	private String introduce;
	
	
	
//	@OneToMany
//	@JoinColumn(name = "AttachedFile_id")
//	private List<AttachedFile> attachedFiles;
	
	
}
