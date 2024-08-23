package com.example.deliciousBee.model.mypage;

import java.util.List;

import com.example.deliciousBee.model.file.AttachedFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class MyPageUpdateForm {
	
	
	private String introduce;
	
	private List<AttachedFile> attachedFiles;
	
	public static  MyPage toMyPage(MyPageUpdateForm myPageUpdateForm) {
		MyPage myPage = new MyPage();
		myPage.setIntroduce(myPageUpdateForm.getIntroduce());
//        myPage.setAttachedFiles(myPageUpdateForm.getAttachedFiles());
		
		return myPage;
	}
}
