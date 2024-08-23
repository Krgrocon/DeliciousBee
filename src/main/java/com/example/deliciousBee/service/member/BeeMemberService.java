package com.example.deliciousBee.service.member;

import java.util.Optional;

import com.example.deliciousBee.model.file.AttachedFile;
import com.example.deliciousBee.model.mypage.MyPage;
import com.example.deliciousBee.repository.FileRepository;
import com.example.deliciousBee.repository.MyPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.deliciousBee.repository.BeeMemberRepository;
import com.example.deliciousBee.model.member.BeeMember;

import jakarta.transaction.Transactional;


@RequiredArgsConstructor
@Service
public class BeeMemberService implements UserDetailsService {


	@Value("${file.upload.path}")
	private String uploadPath;

	private final BeeMemberRepository beeMemberRepository;
	private final FileRepository fileRepository;
	private final MyPageRepository myPageRepository;

	
	@Transactional //에러가 터지면 커밋안해준다 롤백해줌, 해당쿼리를 롤백해줌
	public void saveMember(BeeMember beeMember) {
//		beeMember.setPassword(passwordEncoder.encode(beeMember.getPassword()));
		beeMemberRepository.save(beeMember);  // 저장

	}

	public BeeMember findMemberById(String string) {
		Optional<BeeMember> member =  beeMemberRepository.findById(string);
		return member.orElse(null); //찾았는데 없으면 null 찾았는데 있으면 그 id값 리턴
	}


	@Transactional
	public void saveProfile(BeeMember beeMember, AttachedFile attachedFile) {
		// 첨부 파일이 있을 경우
		if (attachedFile != null) {
			beeMemberRepository.save(beeMember);
			fileRepository.save(attachedFile);
		}
		else {
			beeMemberRepository.save(beeMember);
		}
	}

	//**********************멤버 수정***************
	@Transactional
	public void updateMember(BeeMember updateMember) {

		BeeMember findMember = findMemberById(updateMember.getMember_id());



		findMember.setName(updateMember.getName());
		findMember.setNational(updateMember.getNational());
		findMember.setPassword(updateMember.getPassword());
		findMember.setEmail(updateMember.getEmail());
		beeMemberRepository.save(findMember);

	}

	public boolean isPasswordSame(String member_id, String password) {
		BeeMember member = findMemberById(member_id);
		return member != null && member.getPassword().equals(password);
	}

	public boolean isPasswordMatching(String password, String confirmPassword) {
		return password != null && password.equals(confirmPassword);
	}

	//******************회원탈퇴*******************
	public void deleteMember(String member_id) {
		beeMemberRepository.deleteById(member_id);

	}

	//*****************마이페이지 꾸미기***************************
//	@Transactional
//	public void saveMyPage(MyPage myPage , AttachedFile attachedFile) {
//
//		myPage = myPageRepository.save(myPage); //myPage 먼저저장
//		// 첨부파일이 있는 경우 처리
//		if (attachedFile != null) {
//			attachedFile.setMyPage(myPage); // 첨부파일에 MyPage를 설정
//			fileRepository.save(attachedFile); // 첨부파일 저장
//		}
//		else {
//			myPageRepository.save(myPage);
//		}
//
//
//	}


	@Override
	public UserDetails loadUserByUsername(String member_id) throws UsernameNotFoundException {
		Optional<BeeMember> beeMember = beeMemberRepository.findByMemberid(member_id);
		if (beeMember.isPresent()) {
			return beeMember.get();
		} else {
			throw new UsernameNotFoundException("User not found with username: " + member_id);
		}
	}

}
