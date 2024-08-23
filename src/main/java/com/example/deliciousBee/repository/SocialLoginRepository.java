package com.example.deliciousBee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.model.member.SocialLogin;

public interface SocialLoginRepository extends JpaRepository<SocialLogin, Long> {
    Optional<SocialLogin> findByBeeMember(BeeMember beeMember);
    Optional<SocialLogin> findByEmail(String email);
}