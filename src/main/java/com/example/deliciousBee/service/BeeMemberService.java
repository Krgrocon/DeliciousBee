package com.example.deliciousBee.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.deliciousBee.repository.BeeMemberRepository;
import com.example.deliciousBee.model.member.BeeMember;

import jakarta.transaction.Transactional;


@Service
public class BeeMemberService {
	
	private BeeMemberRepository beeMemberRepository;

	@Autowired
	private void setMemberRepository(BeeMemberRepository beeMemberRepository) {
		this.beeMemberRepository = beeMemberRepository;
	}
	
	@Transactional //에러가 터지면 커밋안해준다 롤백해줌, 해당쿼리를 롤백해줌
	public void saveMember(BeeMember beeMember) {
		beeMemberRepository.save(beeMember);  // 저장
	
	}

	public BeeMember findMemberById(String member_id) {
		Optional<BeeMember> member =  beeMemberRepository.findById(member_id);
		return member.orElse(null); //찾았는데 없으면 null 찾았는데 있으면 그 id값 리턴
	} 
	

	
//**********************멤버 수정***************
	public void updateMember(BeeMember updateMember) {
		
		BeeMember findMember = findMemberById(updateMember.getMember_id());
		
		findMember.setPassword(updateMember.getPassword());
		findMember.setBirth(updateMember.getBirth());
		findMember.setEmail(updateMember.getEmail());
		beeMemberRepository.save(findMember);
	}
	
}
