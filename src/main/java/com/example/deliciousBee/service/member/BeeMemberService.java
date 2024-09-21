package com.example.deliciousBee.service.member;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import com.example.deliciousBee.model.member.Role;
import com.example.deliciousBee.repository.FileRepository;
import com.example.deliciousBee.repository.MemberFileRepository;
import com.example.deliciousBee.repository.MyPageRepository;
import com.example.deliciousBee.util.MemberFileService;
import com.google.api.client.util.Value;
import com.example.deliciousBee.dto.member.OAuthAttributes;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.deliciousBee.repository.BeeMemberRepository;
import com.example.deliciousBee.model.file.MemberAttachedFile;
import com.example.deliciousBee.model.member.BeeMember;

import jakarta.transaction.Transactional;


@RequiredArgsConstructor
@Service
public class BeeMemberService implements UserDetailsService {


	@Value("${file.upload.path}") 
    private String uploadPath; 

	private final BeeMemberRepository beeMemberRepository;
	private final MemberFileRepository memberFileRepository;
	private final MyPageRepository myPageRepository;
	private final MemberFileService memberFileService;
	
	@Transactional // 에러가 터지면 커밋안해준다 롤백해줌, 해당쿼리를 롤백해줌
	public void saveMember(BeeMember beeMember, MemberAttachedFile attachedFile) {
//		beeMember.setPassword(passwordEncoder.encode(beeMember.getPassword()));
		beeMemberRepository.save(beeMember); // 저장

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
	
	
	
	// **********************멤버 수정***************
		@Transactional
		public void updateMember(BeeMember updateMember, boolean isFileRemoved, MultipartFile file) {

			BeeMember findMember = findMemberById(updateMember.getMember_id());

			findMember.setProfileImage(updateMember.getProfileImage());
			findMember.setNickname(updateMember.getNickname());
			findMember.setNational(updateMember.getNational());
			findMember.setEmail(updateMember.getEmail());

			
			// 첨부파일 삭제전 체크
			MemberAttachedFile attachedFile = findFileByMemberId(findMember);
			if (attachedFile != null && (isFileRemoved || (file != null && file.getSize() > 0))) { // 없는경우에도 수정하기때문에 확인작업
				// isFileRemoved:파일삭제요청
				removeFile(attachedFile);

			}

			// 새로운 파일 저장
			 if (file != null && file.getSize() > 0) {
			        try {
			            MemberAttachedFile newSaveFile = memberFileService.saveFile(file);
			            newSaveFile.setBeeMember(findMember);
			            findMember.setProfileImage(newSaveFile);
			            saveMember(findMember, newSaveFile); 
			        } catch (IOException e) {
			            // 예외 처리
			            // 추가적인 오류 처리 로직 (예: 사용자에게 오류 메시지 표시)
			        }
			    }

			beeMemberRepository.save(findMember);

		}

		// 사진찾기
		private MemberAttachedFile findFileByMemberId(BeeMember beeMember) {
			// 쿼리 메서드
 			MemberAttachedFile attachedFile = memberFileRepository.findByBeeMember(beeMember); // findBy로 시작해야 쿼리만들어줌
			
			System.out.println("확인용");
			return attachedFile;
		}
		
		//첨부파일 삭제하는 메서드
			public void removeFile(MemberAttachedFile attachedFile) {
				//첨부파일 삭제(서버, DB 둘다 삭제), 파일삭제를 요청했거나 새로운 파일이 업로드되면 기존파일삭제
				//1)DB에서 삭제
				memberFileRepository.deleteById(attachedFile.getProfilImage_id()); 
				//2)서버(로컬)에서 삭제
				String fullPath = uploadPath + "/" + attachedFile.getSaved_filename();
				memberFileService.deleteFile(fullPath);
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