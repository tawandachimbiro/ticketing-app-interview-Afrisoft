package com.changamire.test;

import com.changamire.event.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Email Test Controller
 * 
 * This controller provides test endpoints to verify email configuration
 * and troubleshoot SMTP connectivity issues.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@RestController
@RequestMapping("/api/test/email")
@RequiredArgsConstructor
@Tag(name = "Email Testing", description = "Test email configuration and connectivity")
public class EmailTestController {
    
    private final JavaMailSender mailSender;
    private final EmailService emailService;

    @Operation(summary = "Test email configuration", description = "Check if email service is properly configured")
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> testEmailConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("mailSender", mailSender != null ? "Configured" : "Not configured");
        config.put("emailService", emailService != null ? "Configured" : "Not configured");
        
        return ResponseEntity.ok(config);
    }

    @Operation(summary = "Send test email", description = "Send a simple test email to verify SMTP connectivity")
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendTestEmail(@RequestParam String to) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String testHtml = """
                    <!DOCTYPE html>
                    <html>
                    <body>
                        <h2>🧪 Test Email</h2>
                        <p>This is a test email from your ticketing application.</p>
                        <p>If you received this, your email configuration is working correctly!</p>
                    </body>
                    </html>""";
            
            emailService.sendTicketConfirmation(
                    to,
                    "Test Email - Ticketing App",
                    testHtml
            );
            
            result.put("success", true);
            result.put("message", "Test email sent successfully to: " + to);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getClass().getName());
            result.put("message", e.getMessage());
            result.put("rootCause", e.getCause() != null ? e.getCause().getMessage() : "No root cause");
            
            // Print full stack trace to console
            System.err.println("=== EMAIL TEST FAILED ===");
            e.printStackTrace();
            
            return ResponseEntity.status(500).body(result);
        }
    }
}
