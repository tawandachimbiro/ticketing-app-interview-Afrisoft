package com.changamire.event;

import com.changamire.exceptions.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendTicketConfirmation(String to, String subject, String htmlContent) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        try {
            helper.setText(htmlContent, true); // true = isHTML
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("replace this with a  @support email");
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendingException("Failed to send email to " + e);
        }
    }








//    public void sendTicketConfirmation(String to, String subject, String content) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(content);
//        mailSender.send(message);
//    }
}