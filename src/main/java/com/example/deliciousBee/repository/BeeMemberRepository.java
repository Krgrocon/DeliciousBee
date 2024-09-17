package com.example.deliciousBee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deliciousBee.model.member.BeeMember;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BeeMemberRepository extends JpaRepository<BeeMember, String> {
    Optional<BeeMember> findByEmail(String email);
    @Query("SELECT b FROM BeeMember b WHERE b.member_id = :member_id")
    Optional<BeeMember> findByMemberid(@Param("member_id") String member_id);
    boolean existsByEmail(String email);
}
