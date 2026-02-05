package com.changamire.event;

import com.changamire.exceptions.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Email Service
 * <p>
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

    /**
     * Send ticket confirmation email asynchronously.
     * <p>
     * This method runs in a separate thread so that ticket purchase
     * responses are not blocked by SMTP latency or failures.
     */
    @Async("emailTaskExecutor")
    public void sendTicketConfirmation(String to, String subject, String htmlContent) {
        System.out.println(" [ASYNC EMAIL] Queued email to: " + to + " with subject: " + subject);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;

        try {
            helper = new MimeMessageHelper(message, true, "utf-8"); // true = multipart email
            helper.setText(htmlContent, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("chimbirotawanda@gmail.com");

            // Add QR code images as embedded resources
            var imagePaths = extractImagePathsFromBody(htmlContent);
            for (var path : imagePaths) {
                var res = new FileSystemResource(new File(path));
                helper.addInline(path, res);
            }

            mailSender.send(message);
            System.out.println("✅ [ASYNC EMAIL] Sent successfully to: " + to);
        } catch (MessagingException e) {
            // Log and wrap in custom exception for visibility; this runs in background thread
            System.err.println("❌ [ASYNC EMAIL] Failed to send email to: " + to);
            System.err.println("Reason: " + e.getMessage());
            throw new EmailSendingException("Failed to send email: " + e.getMessage());
        }
    }

    private List<String> extractImagePathsFromBody(String body) {
        var paths = new ArrayList<String>();
        var p = Pattern.compile("cid:(.*?)['\"]");
        var m = p.matcher(body);
        while (m.find()) {
            paths.add(m.group(1));
        }
        return paths;
    }
}








