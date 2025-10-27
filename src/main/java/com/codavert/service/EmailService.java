package com.codavert.service;

import com.codavert.dto.ContactFormDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${contact.recipient.email:}")
    private String recipientEmail;
    
    @Value("${spring.mail.username:}")
    private String fromEmail;
    
    @Value("${spring.mail.password:}")
    private String mailPassword;
    
    /**
     * Check if email is properly configured
     */
    private boolean isEmailConfigured() {
        boolean configured = fromEmail != null && !fromEmail.isEmpty() 
                && recipientEmail != null && !recipientEmail.isEmpty()
                && mailPassword != null && !mailPassword.isEmpty();
        
        if (!configured) {
            logger.warn("⚠️ Email is not properly configured. Skipping email notification.");
            logger.warn("💡 To enable email notifications, please configure:");
            logger.warn("   - MAIL_USERNAME (spring.mail.username)");
            logger.warn("   - MAIL_PASSWORD (spring.mail.password)");
            logger.warn("   - CONTACT_EMAIL (contact.recipient.email)");
        }
        
        return configured;
    }
    
    /**
     * Send contact form notification email asynchronously
     * This runs in a separate thread so it doesn't block the HTTP response
     */
    @Async
    public void sendContactFormEmail(ContactFormDto contactForm) {
        // Check if email is configured before attempting to send
        if (!isEmailConfigured()) {
            logger.info("📧 Email notification skipped (not configured) for: {}", contactForm.getEmail());
            return;
        }
        
        try {
            logger.info("📧 Attempting to send email FROM: {} TO: {}", fromEmail, recipientEmail);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(recipientEmail);
            helper.setSubject("🔔 New Contact Form Submission - Codavert");
            
            String htmlContent = buildHtmlEmail(contactForm);
            helper.setText(htmlContent, true);
            
            logger.info("📤 Email message prepared, sending via SMTP...");
            mailSender.send(message);
            logger.info("✅ Email sent successfully FROM: {} TO: {}", fromEmail, recipientEmail);
            
        } catch (MessagingException e) {
            logger.warn("❌ Failed to send email - MessagingException: {}", e.getMessage());
            // Don't throw exception - this is async, just log it
        } catch (Exception e) {
            logger.warn("❌ Email sending failed: {} (This is expected if email is not configured on cloud platforms)", e.getMessage());
        }
    }
    
    /**
     * Build HTML email content
     */
    private String buildHtmlEmail(ContactFormDto contactForm) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a");
        String timestamp = now.format(formatter);
        
        // Build company section if present
        String companySection = "";
        if (contactForm.getCompany() != null && !contactForm.getCompany().isEmpty()) {
            companySection = "<div class=\"info-section\">" +
                           "<div class=\"info-label\">🏢 Company</div>" +
                           "<div class=\"info-value\">" + escapeHtml(contactForm.getCompany()) + "</div>" +
                           "</div>";
        }
        
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<style>");
        html.append("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; ");
        html.append("line-height: 1.6; color: #333; background-color: #f4f4f4; margin: 0; padding: 0; }");
        html.append(".container { max-width: 600px; margin: 30px auto; background: #ffffff; border-radius: 10px; ");
        html.append("overflow: hidden; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); }");
        html.append(".header { background: linear-gradient(135deg, #8b45ff 0%, #00d4ff 100%); ");
        html.append("color: white; padding: 30px; text-align: center; }");
        html.append(".header h1 { margin: 0; font-size: 24px; font-weight: 700; }");
        html.append(".content { padding: 30px; }");
        html.append(".info-section { margin-bottom: 25px; }");
        html.append(".info-label { font-size: 12px; font-weight: 600; color: #8b45ff; text-transform: uppercase; ");
        html.append("letter-spacing: 0.5px; margin-bottom: 5px; }");
        html.append(".info-value { font-size: 16px; color: #333; padding: 10px; background: #f8f9fa; ");
        html.append("border-radius: 5px; border-left: 4px solid #8b45ff; }");
        html.append(".message-box { background: #f8f9fa; border-radius: 8px; padding: 20px; ");
        html.append("border-left: 4px solid #00d4ff; margin-top: 15px; white-space: pre-wrap; }");
        html.append(".footer { background: #f8f9fa; padding: 20px; text-align: center; font-size: 12px; ");
        html.append("color: #666; border-top: 1px solid #e0e0e0; }");
        html.append(".badge { display: inline-block; background: #e8f5e9; color: #2e7d32; padding: 5px 12px; ");
        html.append("border-radius: 15px; font-size: 11px; font-weight: 600; margin-top: 10px; }");
        html.append(".timestamp { color: #999; font-size: 13px; margin-top: 10px; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class=\"container\">");
        html.append("<div class=\"header\">");
        html.append("<h1>🔔 New Contact Form Submission</h1>");
        html.append("<div class=\"badge\">New Lead</div>");
        html.append("</div>");
        html.append("<div class=\"content\">");
        html.append("<p style=\"font-size: 16px; color: #666; margin-top: 0;\">");
        html.append("You have received a new message through your website contact form.");
        html.append("</p>");
        html.append("<div class=\"info-section\">");
        html.append("<div class=\"info-label\">👤 Full Name</div>");
        html.append("<div class=\"info-value\">").append(escapeHtml(contactForm.getFullName())).append("</div>");
        html.append("</div>");
        html.append("<div class=\"info-section\">");
        html.append("<div class=\"info-label\">📧 Email Address</div>");
        html.append("<div class=\"info-value\">");
        html.append("<a href=\"mailto:").append(escapeHtml(contactForm.getEmail())).append("\" ");
        html.append("style=\"color: #8b45ff; text-decoration: none;\">");
        html.append(escapeHtml(contactForm.getEmail())).append("</a>");
        html.append("</div>");
        html.append("</div>");
        html.append(companySection);
        html.append("<div class=\"info-section\">");
        html.append("<div class=\"info-label\">💬 Message</div>");
        html.append("<div class=\"message-box\">").append(escapeHtml(contactForm.getMessage())).append("</div>");
        html.append("</div>");
        html.append("<div class=\"timestamp\">");
        html.append("📅 Submitted on ").append(timestamp);
        html.append("</div>");
        html.append("</div>");
        html.append("<div class=\"footer\">");
        html.append("<p style=\"margin: 5px 0;\">");
        html.append("This email was sent from your <strong>Codavert</strong> website contact form.");
        html.append("</p>");
        html.append("<p style=\"margin: 5px 0; color: #999;\">");
        html.append("Please respond to the inquiry as soon as possible.");
        html.append("</p>");
        html.append("</div>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        
        return html.toString();
    }
    
    /**
     * Escape HTML special characters
     */
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;");
    }
    
    /**
     * Send simple text email (fallback)
     */
    public void sendSimpleContactFormEmail(ContactFormDto contactForm) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(recipientEmail);
            message.setSubject("New Contact Form Submission - Codavert");
            
            String textContent = buildTextEmail(contactForm);
            message.setText(textContent);
            
            mailSender.send(message);
            logger.info("Simple contact form email sent successfully to {}", recipientEmail);
            
        } catch (Exception e) {
            logger.error("Failed to send simple contact form email", e);
            throw new RuntimeException("Failed to send email notification", e);
        }
    }
    
    /**
     * Build plain text email content
     */
    private String buildTextEmail(ContactFormDto contactForm) {
        StringBuilder sb = new StringBuilder();
        sb.append("NEW CONTACT FORM SUBMISSION\n");
        sb.append("=".repeat(50)).append("\n\n");
        sb.append("Full Name: ").append(contactForm.getFullName()).append("\n");
        sb.append("Email: ").append(contactForm.getEmail()).append("\n");
        
        if (contactForm.getCompany() != null && !contactForm.getCompany().isEmpty()) {
            sb.append("Company: ").append(contactForm.getCompany()).append("\n");
        }
        
        sb.append("\nMessage:\n");
        sb.append("-".repeat(50)).append("\n");
        sb.append(contactForm.getMessage()).append("\n");
        sb.append("-".repeat(50)).append("\n\n");
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a");
        sb.append("Submitted on: ").append(now.format(formatter)).append("\n");
        
        return sb.toString();
    }
}

