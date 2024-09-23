package com.example.deliciousBee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deliciousBee.model.file.MyPageAttachedFile;
import com.example.deliciousBee.model.mypage.MyPage;

public interface MyPageFileRepository extends JpaRepository<MyPageAttachedFile, Long>{

	MyPageAttachedFile findByMyPage(MyPage myPage);
	}
