package com.example.deliciousBee.service.member;

import java.util.Optional;
import java.util.UUID;

import com.example.deliciousBee.model.member.Role;
import com.example.deliciousBee.repository.FileRepository;
import com.example.deliciousBee.repository.MyPageRepository;
import com.example.deliciousBee.dto.member.OAuthAttributes;
import lombok.RequiredArgsConstructor;
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


	


	private final BeeMemberRepository beeMemberRepository;
	private final FileRepository fileRepository;
	private final MyPageRepository myPageRepository;

	
	@Transactional //에러가 터지면 커밋안해준다 롤백해줌, 해당쿼리를 롤백해줌
	public void saveMember(BeeMember beeMember) {
//		beeMember.setPassword(passwordEncoder.encode(beeMember.getPassword()));
		beeMemberRepository.save(beeMember);  // 저장



	}
	public boolean existsByEmail(String email) {
		return beeMemberRepository.existsByEmail(email);
	}
	public BeeMember findMemberById(String string) {
		Optional<BeeMember> member =  beeMemberRepository.findById(string);
		return member.orElse(null); //찾았는데 없으면 null 찾았는데 있으면 그 id값 리턴
	}

	public BeeMember findMemberByEmail(String string) {
		Optional<BeeMember> member =  beeMemberRepository.findByEmail(string);
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
	// BeeMember 찾기 또는 생성
	@Transactional
	public BeeMember findOrCreateBeeMember(OAuthAttributes attributes) {
		return beeMemberRepository.findByEmail(attributes.getEmail())
				.orElseGet(() -> {
					BeeMember newBeeMember = BeeMember.builder()
							.member_id(UUID.randomUUID().toString())
							.password(UUID.randomUUID().toString()) // 비밀번호는 필요 없을 경우 null로 설정
							.email(attributes.getEmail())
							.nickname(attributes.getName())
							.isSocialUser(true)
							.role(Role.USER)
							.build();

					return beeMemberRepository.save(newBeeMember);
				});
	}



//	@Override
//	public UserDetails loadUserByUsername(String member_id) throws UsernameNotFoundException {
//		Optional<BeeMember> beeMember = beeMemberRepository.findByMemberid(member_id);
//		if (beeMember.isPresent()) {
//			return beeMember.get();
//		} else {
//			throw new UsernameNotFoundException("User not found with username: " + member_id);
//		}
//	}

	@Override
	public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
		BeeMember beeMember = beeMemberRepository.findByMemberid(memberId)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with member_id: " + memberId));

		return beeMember;
	}


}
