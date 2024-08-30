package com.example.deliciousBee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deliciousBee.model.file.MemberAttachedFile;
import com.example.deliciousBee.model.mypage.MyPage;

public interface MemberFileRepository extends JpaRepository<MemberAttachedFile, Long> {

	MemberAttachedFile findByMyPage(MyPage myPage);
}
