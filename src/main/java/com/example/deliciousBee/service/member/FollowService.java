package com.example.deliciousBee.service.member;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.model.member.Follow;
import com.example.deliciousBee.repository.BeeMemberRepository;
import com.example.deliciousBee.repository.FollowRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FollowService {

	private final FollowRepository followRepository;
	private final BeeMemberRepository beeMemberRepository;

	// 팔로우
	public void follow(String followerId, String followingId) {
		BeeMember follower = beeMemberRepository.findById(followerId)
				.orElseThrow(() -> new UsernameNotFoundException("Follower not found."));
		BeeMember following = beeMemberRepository.findById(followingId)
				.orElseThrow(() -> new UsernameNotFoundException("Following not found."));

		Follow follow = new Follow(follower, following);
		followRepository.save(follow);
	}

	// 언팔로우
	public void unfollow(String followerId, String followingId) {
		followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
	}

	// 팔로우 여부 확인
	public boolean isFollowing(String followerId, String followingId) {
		return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
	}
	 // 특정 사용자가 팔로우하는 사용자 목록 조회
    public List<BeeMember> getFollowingList(String followerId) {
        return followRepository.findByFollowerMemberId(followerId)
                .stream()
                .map(Follow::getFollowing)
                .toList();
    }
}