package com.example.deliciousBee.service.member;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.deliciousBee.model.file.AttachedFile;
import com.example.deliciousBee.model.file.MemberAttachedFile;
import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.model.mypage.MyPage;
import com.example.deliciousBee.repository.BeeMemberRepository;
import com.example.deliciousBee.repository.FileRepository;
import com.example.deliciousBee.repository.MemberFileRepository;
import com.example.deliciousBee.repository.MyPageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MyPageService implements UserDetailsService {

	@Value("${file.upload.path}")
	private String uploadPath;

	private final BeeMemberRepository beeMemberRepository;
	private final MemberFileRepository memberFileRepository;
	private final MyPageRepository myPageRepository;

	
	// *****************마이페이지 생성***************************
	@Transactional
	public void saveMyPage(MyPage myPage) {

		myPageRepository.save(myPage);

	}

	// *****************마이페이지 수정******************
	@Transactional
	public void updateMyPage(MyPage myPage, MemberAttachedFile attachedFile) {
		// 새 첨부 파일 저장
		 if (attachedFile != null) {
			 if ("PROFILE".equals(attachedFile.getImageType())) {
	                updateProfileImage(myPage, attachedFile);
	            } else if ("MAIN".equals(attachedFile.getImageType())) {
	                updateMainImage(myPage, attachedFile);
	            } else if ("BACKGROUND".equals(attachedFile.getImageType())) {
	                updateBackgroundImage(myPage, attachedFile);
		    }
		 }
	     else {
	    	 myPageRepository.save(myPage);
	     }
	}
	
	
	@Transactional
	public void updateProfileImage(MyPage myPage, MemberAttachedFile attachedFile) {
	    memberFileRepository.save(attachedFile);
	    myPage.setProfileImage(attachedFile); // MyPage 객체의 profileImage 필드 업데이트
	    myPageRepository.save(myPage); // MyPage 객체 저장
	}

	@Transactional
	public void updateMainImage(MyPage myPage, MemberAttachedFile attachedFile) {
	    memberFileRepository.save(attachedFile);
	    myPage.setMainImage(attachedFile); // MyPage 객체의 mainImage 필드 업데이트
	    myPageRepository.save(myPage); // MyPage 객체 저장
	}

	@Transactional
	public void updateBackgroundImage(MyPage myPage, MemberAttachedFile attachedFile) {
	    memberFileRepository.save(attachedFile);
	    myPage.setBackgroundImage(attachedFile); // MyPage 객체의 backgroundImage 필드 업데이트
	    myPageRepository.save(myPage); // MyPage 객체 저장
	}
	
	
//******************************************************
	// V4(페이징 추가) 게시물 전체 목록 *(Map->List) 변환해서 리턴
	public Page<MyPage> findAll(Pageable pageable) { // 페이징 List->Page
		Page<MyPage> page = myPageRepository.findAll(pageable);
		return page;
	}

	public MyPage findById(Long myPage_id) {
		Optional<MyPage> myPage = myPageRepository.findById(myPage_id);
		return myPage.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 마이페이지입니다."));
	}

	@Override
	public UserDetails loadUserByUsername(String member_id) throws UsernameNotFoundException {
		Optional<BeeMember> beeMember = beeMemberRepository.findByMemberid(member_id);
		if (beeMember.isPresent()) {
			return beeMember.get();
		} else {
			throw new UsernameNotFoundException("User not found with username: " + member_id);
		}
	}

	public List<MyPage> findMyPagesOrderByReviewCountDesc() {
		return null;
	}
	
	
	
}
