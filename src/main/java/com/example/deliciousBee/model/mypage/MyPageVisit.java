package com.example.deliciousBee.model.mypage;

import java.time.LocalDate;

import com.example.deliciousBee.model.member.BeeMember;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MyPageVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "my_page_id")
    private MyPage myPage;

    @ManyToOne
    @JoinColumn(name = "visitor_id")
    private BeeMember visitor; // 방문한 사용자

    private LocalDate visitDate; 
    
    public MyPageVisit(MyPage myPage, BeeMember visitor) {
        this.myPage = myPage;
        this.visitor = visitor;
        this.visitDate = LocalDate.now(); // 현재 날짜로 초기화;
    }
}