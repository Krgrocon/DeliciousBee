package com.example.deliciousBee.service.member;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private static final String SENDER_EMAIL = "delibe25@gmail.com";
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    // 랜덤으로 6자리 숫자 생성
    public int generateVerificationCode() {
        Random random = new Random();
        return 100000 + random.nextInt(900000); // 100000 ~ 999999
    }

    // 이메일 생성
    public MimeMessage createMail(String recipientEmail, int verificationCode) throws MessagingException {
        logger.info("Creating email for: {}", recipientEmail);
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(SENDER_EMAIL);
        message.setRecipients(MimeMessage.RecipientType.TO, recipientEmail);
        message.setSubject("DeliciousBee 회원가입 인증번호");

        String body = "<h3>요청하신 인증 번호입니다.</h3>"
                + "<h1>" + verificationCode + "</h1>"
                + "<h3>감사합니다.</h3>";

        message.setText(body, "UTF-8", "html");
        logger.info("Email created successfully for: {}", recipientEmail);

        return message;
    }

    // 이메일 전송
    public void sendMail(String recipientEmail, int verificationCode) throws MessagingException {
        logger.info("Sending email to: {}", recipientEmail);
        MimeMessage message = createMail(recipientEmail, verificationCode);
        javaMailSender.send(message);
        logger.info("Email sent successfully to: {}", recipientEmail);
    }
}
