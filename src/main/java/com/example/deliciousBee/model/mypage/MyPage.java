package com.example.deliciousBee.model.mypage;

import java.time.LocalDateTime;
import java.util.List;

import com.example.deliciousBee.model.file.MemberAttachedFile;
import com.example.deliciousBee.model.member.BeeMember;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

	
	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "mainImage_id") // 외래 키 컬럼 이름 지정
    private MemberAttachedFile mainImage;

	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profileImage_id") // 외래 키 컬럼 이름 지정
    private MemberAttachedFile profileImage;

	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "backgroundImage_id") // 외래 키 컬럼 이름 지정
    private MemberAttachedFile backgroundImage;


//	private Long hit = 0L;                   //조회수
//	
//	//엔티티라 부름 vO는 값만 가지고있는거 겟터셋터같은거
//	public void addHit() {
//		//조회수 증가
//		this.hit++;
//	}

	public static MyPageUpdateForm toUpdateForm(MyPage myPage) {
		MyPageUpdateForm myPageUpdateForm = new MyPageUpdateForm();

		myPageUpdateForm.setMyPage_id(myPage.getId());
		myPageUpdateForm.setIntroduce(myPage.getIntroduce());
		myPageUpdateForm.setBeeMember(myPage.getBeeMember());
		myPageUpdateForm.setProfileImage(myPage.getProfileImage());
		myPageUpdateForm.setMainImage(myPage.getMainImage());
		myPageUpdateForm.setBackgroundImage(myPage.getBackgroundImage());
//		myPageUpdateForm.setHit(myPage.getHit());


		return myPageUpdateForm;

	}

}
