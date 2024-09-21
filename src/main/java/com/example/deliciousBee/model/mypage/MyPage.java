package com.example.deliciousBee.model.mypage;

import java.time.LocalDateTime;
import java.util.List;

import com.example.deliciousBee.model.file.MyPageAttachedFile;
import com.example.deliciousBee.model.member.BeeMember;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.minidev.json.annotate.JsonIgnore;

@Entity
@Getter
@Setter
@ToString
public class MyPage {

	@OneToOne(cascade = CascadeType.ALL) // beeMember회원탈퇴할때 myPage도같이삭제
	@JoinColumn(name = "member_id")
	private BeeMember beeMember;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 생성 전략 설정
	private Long id;

	private String introduce;

	
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "mainImage_id")
	private MyPageAttachedFile mainImage;


	private Long hit = 0L;  // 조회수
	
	public MyPage() {
	        this.hit = 0L; 
	    }
	
	@OneToMany(mappedBy = "myPage", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER) 
	private List<MyPageVisit> visits; // MyPage를 방문한 기록
	
	
	 

	public static MyPageUpdateForm toUpdateForm(MyPage myPage, BeeMember beeMember) {
		MyPageUpdateForm myPageUpdateForm = new MyPageUpdateForm();

		myPageUpdateForm.setMyPage_id(myPage.getId());
		myPageUpdateForm.setIntroduce(myPage.getIntroduce());
		myPageUpdateForm.setBeeMember(myPage.getBeeMember());

		return myPageUpdateForm;

	}

}
