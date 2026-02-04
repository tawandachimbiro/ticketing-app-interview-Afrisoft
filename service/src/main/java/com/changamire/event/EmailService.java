package com.changamire.event;

import com.changamire.exceptions.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Email Service
 * 
 * This service handles sending email notifications to customers, including
 * ticket confirmations with embedded QR codes. It supports HTML emails
 * with inline images and attachments.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendTicketConfirmation(String to, String subject, String htmlContent) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;

        try {
            helper = new MimeMessageHelper(message, true, "utf-8"); // true = multipart email
            helper.setText(htmlContent, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("chimbirotawanda@gmail.com");

            // Add QR code images as embedded resources
            List<String> imagePaths = extractImagePathsFromBody(htmlContent);
            for (String path : imagePaths) {
                FileSystemResource res = new FileSystemResource(new File(path));
                helper.addInline(path, res);
            }

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendingException("Failed to send email: " + e.getMessage());
        }
    }

    private List<String> extractImagePathsFromBody(String body) {
        List<String> paths = new ArrayList<>();
        Pattern p = Pattern.compile("cid:(.*?)['\"]");
        Matcher m = p.matcher(body);
        while (m.find()) {
            paths.add(m.group(1));
        }
        return paths;
    }
}



//package com.changamire.event;
//
//import com.changamire.exceptions.EmailSendingException;
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import lombok.RequiredArgsConstructor;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class EmailService {
//
//    private final JavaMailSender mailSender;
//
//    public void sendTicketConfirmation(String to, String subject, String htmlContent) {
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
//
//        try {
//            helper.setText(htmlContent, true); // true = isHTML
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setFrom("chimbirotawanda@gmail.com");
//            mailSender.send(message);
//        } catch (MessagingException e) {
//            throw new EmailSendingException("Failed to send email to " + e);
//        }
//    }
//
//
//}
//





