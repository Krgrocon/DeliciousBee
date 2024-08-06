package com.example.deliciousBee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deliciousBee.model.member.BeeMember;

public interface BeeMemberRepository extends JpaRepository<BeeMember, String> {

}
