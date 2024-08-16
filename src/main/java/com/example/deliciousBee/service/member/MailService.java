package com.example.deliciousBee.service.member;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private static final String senderEmail= "delibe25@gmail.com";
    private static int number;

    // 랜덤으로 숫자 생성
    public static void createNumber() {
        number = (int)(Math.random() * (90000)) + 100000; //(int) Math.random() * (최댓값-최소값+1) + 최소값
    }

    public MimeMessage CreateMail(String mail) {
        createNumber();
        System.out.println("확인용1");
        MimeMessage message = javaMailSender.createMimeMessage();
        System.out.println("확인용1");

        try {
            System.out.println("확인용1");
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            System.out.println("수신자 이메일 주소: " + mail); // 디버깅 로그 추가
            System.out.println("확인용1");
            message.setSubject("이메일 인증");
            String body = "";
            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + number + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body,"UTF-8", "html");
            System.out.println("확인용1");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }

    public int sendMail(String mail) {
        System.out.println("sendMail 메서드의 mail 변수: " + mail);
        MimeMessage message = CreateMail(mail);
        System.out.println("확인용2");
        try {
            javaMailSender.send(message);
            System.out.println("확인용2"); // 성공하면 출력됩니다.
        } catch (Exception e) {
            System.err.println("이메일 전송 오류: " + e.getMessage());
            e.printStackTrace(); // 자세한 내용은 스택 트레이스를 출력합니다.
        }

        return number;
    }
}