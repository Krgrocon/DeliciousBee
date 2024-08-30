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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.deliciousBee.repository.BeeMemberRepository;
import com.example.deliciousBee.model.member.BeeMember;

import jakarta.transaction.Transactional;


@RequiredArgsConstructor
@Service
public class BeeMemberService implements UserDetailsService {


	


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

	//*********************세션 업데이트*******************
	public BeeMember updateSessionMember(String member_id) {
	    return findMemberById(member_id); // 이 메서드는 DB에서 최신 BeeMember 정보를 가져오는 메서드
	}
	
	
	
	//**********************멤버 수정***************
	@Transactional
	public void updateMember(BeeMember updateMember) {

		BeeMember findMember = findMemberById(updateMember.getMember_id());

		
		findMember.setNickname(updateMember.getNickname());
		findMember.setNational(updateMember.getNational());
		findMember.setEmail(updateMember.getEmail());
		
		
		beeMemberRepository.save(findMember);
		

	}

	
	

	
	//****************비밀번호 변경
	@Transactional
	public void updatePassword(BeeMember updatePassword) {
		BeeMember findMember = findMemberById(updatePassword.getMember_id());
		findMember.setPassword(updatePassword.getPassword());
		
		
		beeMemberRepository.save(findMember);
	}
	
	
	//******************회원탈퇴*******************
	@Transactional
	public void deleteMember(String member_id) {
		beeMemberRepository.deleteById(member_id);

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

}
