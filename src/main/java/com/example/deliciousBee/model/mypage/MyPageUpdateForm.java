package com.example.deliciousBee.model.mypage;

import java.util.List;

import com.example.deliciousBee.model.file.AttachedFile;
import com.example.deliciousBee.model.file.MemberAttachedFile;
import com.example.deliciousBee.model.member.BeeMember;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MyPageUpdateForm {

	private BeeMember beeMember;

	private Long myPage_id;

	private String introduce;

	private MemberAttachedFile mainImage;
	private MemberAttachedFile profileImage;
	private MemberAttachedFile backgroundImage;

	private boolean fileRemoved; // 기본값 false 삭제됬으면 ture 반환, boolea의 getter는 get이아니라 is

	private Long hit;

	public static MyPage toMyPage(MyPageUpdateForm myPageUpdateForm) {
		MyPage myPage = new MyPage();

		myPage.setId(myPageUpdateForm.getMyPage_id());
		myPage.setIntroduce(myPageUpdateForm.getIntroduce());
		myPage.setBeeMember(myPageUpdateForm.getBeeMember());
		myPage.setMainImage(myPageUpdateForm.getMainImage()); // mainImage 설정
        myPage.setProfileImage(myPageUpdateForm.getProfileImage()); // profileImage 설정
        myPage.setBackgroundImage(myPageUpdateForm.getBackgroundImage()); // backgroundImage 설정
//		myPage.setHit(myPageUpdateForm.getHit());

		return myPage;
	}
}
