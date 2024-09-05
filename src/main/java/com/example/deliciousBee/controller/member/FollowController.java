package com.example.deliciousBee.controller.member;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.service.member.FollowService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    // 팔로우
    @PostMapping("/follow/{followingId}")
    public ResponseEntity<Boolean> follow(@AuthenticationPrincipal BeeMember loginMember, @PathVariable("followingId") String followingId) {
        followService.follow(loginMember.getMember_id(), followingId);
        return ResponseEntity.ok(true); // 팔로우 성공 시 true 반환
    }

    // 언팔로우
    @PostMapping("/unfollow/{followingId}")
    public ResponseEntity<Boolean> unfollow(@AuthenticationPrincipal BeeMember loginMember, @PathVariable("followingId") String followingId) {
        followService.unfollow(loginMember.getMember_id(), followingId);
        return ResponseEntity.ok(true); // 언팔로우 성공 시 true 반환
    }
}