package com.codavert.service;

import com.codavert.dto.ContactFormDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ByteArrayResource;
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

	@Value("${spring.mail.host:smtp.gmail.com}")
	private String mailHost;
    
    /**
     * Check if email is properly configured
     */
	private boolean isEmailConfigured() {
		boolean hasFrom = fromEmail != null && !fromEmail.isEmpty();
		boolean hasRecipient = recipientEmail != null && !recipientEmail.isEmpty();
		boolean hasPassword = mailPassword != null && !mailPassword.isEmpty();
		boolean configured = hasFrom && hasRecipient && hasPassword;

		if (!configured) {
			StringBuilder missing = new StringBuilder();
			if (!hasFrom) missing.append("spring.mail.username ");
			if (!hasRecipient) missing.append("contact.recipient.email ");
			if (!hasPassword) missing.append("spring.mail.password ");
			logger.warn("Email not configured. Missing: {}. Host={}, from={}, recipientSet={}, passwordSet={}",
					missing.toString().trim(), mailHost, fromEmail, hasRecipient, hasPassword);
		}

		return configured;
	}
    
    /**
     * Generic method to send a simple email
     */
    @Async
    public void sendEmail(String to, String subject, String body) {
        // Check if email is configured before attempting to send
        if (!isEmailConfigured()) {
            logger.warn("Skipping email notification (not configured) for recipient: {}", to);
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            
            logger.info("Sending email to {}", to);
            mailSender.send(message);
            logger.info("✅ Email sent successfully to {}", to);
            
        } catch (MailException e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected failure while sending email to {}: {}", to, e.getMessage(), e);
        }
    }
    
    /**
     * Send contact form notification email asynchronously
     * This runs in a separate thread so it doesn't block the HTTP response
     */
    @Async
    public void sendContactFormEmail(ContactFormDto contactForm) {
		// Check if email is configured before attempting to send
		if (!isEmailConfigured()) {
			logger.warn("Skipping email notification (not configured) for submitter: {}", contactForm.getEmail());
			return;
		}

		logger.info("Preparing contact email. host={}, from={}, to={}", mailHost, fromEmail, recipientEmail);
		
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setFrom(fromEmail);
			helper.setTo(recipientEmail);
			helper.setSubject("🔔 New Contact Form Submission - Codavert");
			// Make replies go to the submitter
			if (contactForm.getEmail() != null && !contactForm.getEmail().isEmpty()) {
				helper.setReplyTo(contactForm.getEmail());
			}
			String htmlContent = buildHtmlEmail(contactForm);
			helper.setText(htmlContent, true);
			logger.info("Sending contact email to {}", recipientEmail);
			mailSender.send(message);
			logger.info("✅ Contact email sent successfully to {}", recipientEmail);
		} catch (MessagingException e) {
			logger.error("Failed to construct MIME email: {}", e.getMessage(), e);
			attemptSimpleFallback(contactForm);
		} catch (MailException e) {
			logger.error("SMTP send failed: {}", e.getMessage(), e);
			attemptSimpleFallback(contactForm);
		} catch (Exception e) {
			logger.error("Unexpected failure while sending email: {}", e.getMessage(), e);
			attemptSimpleFallback(contactForm);
		}
    }

	private void attemptSimpleFallback(ContactFormDto contactForm) {
		try {
			logger.warn("Attempting simple text email fallback to {}", recipientEmail);
			sendSimpleContactFormEmail(contactForm);
		} catch (Exception ex) {
			logger.error("Fallback simple email failed: {}", ex.getMessage(), ex);
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
    
    /**
     * Send offer letter email to candidate
     */
    @Async
    public void sendOfferLetterEmail(String candidateEmail, String candidateName, String position, 
                                     String offerDetails, String pdfBase64) {
        if (!isEmailConfigured()) {
            logger.warn("Skipping offer letter email (not configured) for: {}", candidateEmail);
            return;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(candidateEmail);
            helper.setSubject("🎉 Congratulations! Job Offer from Codavert - " + position);
            
            String htmlContent = buildOfferLetterEmail(candidateName, position, offerDetails);
            helper.setText(htmlContent, true);
            
            // Attach PDF if provided
            if (pdfBase64 != null && !pdfBase64.isEmpty()) {
                byte[] pdfBytes = java.util.Base64.getDecoder().decode(pdfBase64);
                helper.addAttachment("Offer_Letter.pdf", new ByteArrayResource(pdfBytes), "application/pdf");
            }
            
            logger.info("Sending offer letter email to {}", candidateEmail);
            mailSender.send(message);
            logger.info("✅ Offer letter email sent successfully to {}", candidateEmail);
            
        } catch (Exception e) {
            logger.error("Failed to send offer letter email to {}: {}", candidateEmail, e.getMessage(), e);
        }
    }
    
    /**
     * Send interview invitation email with Google Meet link
     */
    @Async
    public void sendInterviewInvitationEmail(String candidateEmail, String candidateName, String position,
                                             String interviewDate, String interviewTime, String meetLink, 
                                             String notes) {
        if (!isEmailConfigured()) {
            logger.warn("Skipping interview invitation email (not configured) for: {}", candidateEmail);
            return;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(candidateEmail);
            helper.setSubject("Interview Invitation - " + position + " at Codavert");
            
            String htmlContent = buildInterviewInvitationEmail(candidateName, position, interviewDate, 
                                                              interviewTime, meetLink, notes);
            helper.setText(htmlContent, true);
            
            logger.info("Sending interview invitation email to {}", candidateEmail);
            mailSender.send(message);
            logger.info("✅ Interview invitation email sent successfully to {}", candidateEmail);
            
        } catch (Exception e) {
            logger.error("Failed to send interview invitation email to {}: {}", candidateEmail, e.getMessage(), e);
        }
    }
    
    /**
     * Build HTML email for offer letter
     */
    private String buildOfferLetterEmail(String candidateName, String position, String offerDetails) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<style>");
        html.append("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; ");
        html.append("line-height: 1.6; color: #333; background-color: #f4f4f4; margin: 0; padding: 20px; }");
        html.append(".container { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 10px; ");
        html.append("overflow: hidden; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); }");
        html.append(".header { background: linear-gradient(135deg, #8b45ff 0%, #00d4ff 100%); ");
        html.append("color: white; padding: 30px; text-align: center; }");
        html.append(".content { padding: 30px; }");
        html.append(".button { display: inline-block; background: linear-gradient(135deg, #8b45ff 0%, #00d4ff 100%); ");
        html.append("color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; ");
        html.append("font-weight: 600; margin: 20px 0; }");
        html.append(".footer { background: #f8f9fa; padding: 20px; text-align: center; font-size: 12px; ");
        html.append("color: #666; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class=\"container\">");
        html.append("<div class=\"header\">");
        html.append("<h1 style=\"margin: 0; font-size: 24px;\">🎉 Congratulations!</h1>");
        html.append("</div>");
        html.append("<div class=\"content\">");
        html.append("<p>Dear ").append(escapeHtml(candidateName)).append(",</p>");
        html.append("<p>We are pleased to offer you the position of <strong>").append(escapeHtml(position));
        html.append("</strong> at Codavert!</p>");
        html.append("<p>Please find the detailed offer letter attached to this email.</p>");
        if (offerDetails != null && !offerDetails.isEmpty()) {
            html.append("<div style=\"background: #f8f9fa; padding: 15px; border-radius: 8px; margin: 20px 0;\">");
            html.append(escapeHtml(offerDetails));
            html.append("</div>");
        }
        html.append("<p>We look forward to welcoming you to our team!</p>");
        html.append("<p>Best regards,<br><strong>Codavert Team</strong></p>");
        html.append("</div>");
        html.append("<div class=\"footer\">");
        html.append("<p>This is an automated email from Codavert.</p>");
        html.append("</div>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }
    
    /**
     * Send rejection email to candidate
     */
    @Async
    public void sendRejectionEmail(String candidateEmail, String candidateName, String position, String notes) {
        if (!isEmailConfigured()) {
            logger.warn("Skipping rejection email (not configured) for: {}", candidateEmail);
            return;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(candidateEmail);
            helper.setSubject("Update on Your Application - " + position + " at Codavert");
            
            String htmlContent = buildRejectionEmail(candidateName, position, notes);
            helper.setText(htmlContent, true);
            
            logger.info("Sending rejection email to {}", candidateEmail);
            mailSender.send(message);
            logger.info("✅ Rejection email sent successfully to {}", candidateEmail);
            
        } catch (Exception e) {
            logger.error("Failed to send rejection email to {}: {}", candidateEmail, e.getMessage(), e);
        }
    }
    
    /**
     * Build HTML email for rejection
     */
    private String buildRejectionEmail(String candidateName, String position, String notes) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("<style>");
        html.append("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; ");
        html.append("line-height: 1.6; color: #1a1a1a; background-color: #f5f5f5; margin: 0; padding: 0; }");
        html.append(".email-wrapper { max-width: 600px; margin: 0 auto; background: #ffffff; }");
        html.append(".header { background: #1a1a1a; color: #ffffff; padding: 40px 30px; text-align: center; }");
        html.append(".header h1 { margin: 0; font-size: 28px; font-weight: 600; letter-spacing: -0.5px; }");
        html.append(".header .subtitle { margin: 8px 0 0 0; font-size: 14px; color: #b0b0b0; font-weight: 400; }");
        html.append(".content { padding: 40px 30px; background: #ffffff; }");
        html.append(".greeting { font-size: 16px; color: #1a1a1a; margin-bottom: 20px; }");
        html.append(".intro-text { font-size: 15px; color: #4a4a4a; margin-bottom: 30px; line-height: 1.7; }");
        html.append(".notes-section { background: #fff9e6; border-left: 4px solid #ffc107; ");
        html.append("padding: 20px; border-radius: 4px; margin: 30px 0; }");
        html.append(".notes-section p { font-size: 14px; color: #856404; margin: 0; white-space: pre-wrap; line-height: 1.6; }");
        html.append(".closing { font-size: 15px; color: #4a4a4a; margin-top: 32px; }");
        html.append(".signature { margin-top: 24px; }");
        html.append(".signature-name { font-size: 15px; font-weight: 600; color: #1a1a1a; margin: 4px 0; }");
        html.append(".signature-title { font-size: 13px; color: #6c757d; }");
        html.append(".footer { background: #f8f9fa; padding: 24px 30px; text-align: center; border-top: 1px solid #e9ecef; }");
        html.append(".footer-text { font-size: 12px; color: #6c757d; margin: 0; line-height: 1.6; }");
        html.append(".footer-company { font-size: 13px; font-weight: 600; color: #1a1a1a; margin-bottom: 4px; }");
        html.append("@media only screen and (max-width: 600px) { ");
        html.append(".content { padding: 30px 20px; } ");
        html.append(".header { padding: 30px 20px; } ");
        html.append(".footer { padding: 20px; } ");
        html.append("} ");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class=\"email-wrapper\">");
        html.append("<div class=\"header\">");
        html.append("<h1>Application Update</h1>");
        html.append("<p class=\"subtitle\">Codavert - Professional Software Development</p>");
        html.append("</div>");
        html.append("<div class=\"content\">");
        html.append("<p class=\"greeting\">Dear ").append(escapeHtml(candidateName)).append(",</p>");
        html.append("<p class=\"intro-text\">Thank you for your interest in the <strong>").append(escapeHtml(position));
        html.append("</strong> position at Codavert and for taking the time to apply.</p>");
        html.append("<p class=\"intro-text\">After careful consideration of all applications, we have decided to move forward with other candidates whose qualifications more closely match our current needs.</p>");
        if (notes != null && !notes.isEmpty()) {
            html.append("<div class=\"notes-section\">");
            html.append("<p>").append(escapeHtml(notes)).append("</p>");
            html.append("</div>");
        }
        html.append("<p class=\"closing\">We appreciate your interest in Codavert and encourage you to apply for future positions that may be a better fit for your skills and experience.</p>");
        html.append("<div class=\"signature\">");
        html.append("<div class=\"signature-name\">Best regards,</div>");
        html.append("<div class=\"signature-name\">Codavert Recruitment Team</div>");
        html.append("<div class=\"signature-title\">Human Resources</div>");
        html.append("</div>");
        html.append("</div>");
        html.append("<div class=\"footer\">");
        html.append("<p class=\"footer-company\">Codavert</p>");
        html.append("<p class=\"footer-text\">This is an automated email. Please do not reply directly to this message.</p>");
        html.append("</div>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }
    
    /**
     * Build HTML email for interview invitation
     */
    private String buildInterviewInvitationEmail(String candidateName, String position, String interviewDate,
                                                 String interviewTime, String meetLink, String notes) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("<style>");
        html.append("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; ");
        html.append("line-height: 1.6; color: #1a1a1a; background-color: #f5f5f5; margin: 0; padding: 0; }");
        html.append(".email-wrapper { max-width: 600px; margin: 0 auto; background: #ffffff; }");
        html.append(".header { background: #1a1a1a; color: #ffffff; padding: 40px 30px; text-align: center; }");
        html.append(".header h1 { margin: 0; font-size: 28px; font-weight: 600; letter-spacing: -0.5px; }");
        html.append(".header .subtitle { margin: 8px 0 0 0; font-size: 14px; color: #b0b0b0; font-weight: 400; }");
        html.append(".content { padding: 40px 30px; background: #ffffff; }");
        html.append(".greeting { font-size: 16px; color: #1a1a1a; margin-bottom: 20px; }");
        html.append(".intro-text { font-size: 15px; color: #4a4a4a; margin-bottom: 30px; line-height: 1.7; }");
        html.append(".details-card { background: #f8f9fa; border: 1px solid #e9ecef; border-radius: 8px; ");
        html.append("padding: 24px; margin: 30px 0; }");
        html.append(".detail-row { display: flex; align-items: flex-start; margin-bottom: 16px; padding-bottom: 16px; ");
        html.append("border-bottom: 1px solid #e9ecef; }");
        html.append(".detail-row:last-child { margin-bottom: 0; padding-bottom: 0; border-bottom: none; }");
        html.append(".detail-label { font-size: 12px; font-weight: 600; color: #6c757d; text-transform: uppercase; ");
        html.append("letter-spacing: 0.5px; min-width: 80px; margin-right: 20px; }");
        html.append(".detail-value { font-size: 15px; color: #1a1a1a; font-weight: 500; flex: 1; }");
        html.append(".meet-button-container { text-align: center; margin: 32px 0; }");
        html.append(".meet-button { display: inline-block; background: #00832d; color: #ffffff; ");
        html.append("padding: 14px 32px; text-decoration: none; border-radius: 6px; ");
        html.append("font-weight: 600; font-size: 15px; letter-spacing: 0.3px; ");
        html.append("box-shadow: 0 2px 4px rgba(0, 131, 45, 0.2); transition: all 0.2s; }");
        html.append(".meet-button:hover { background: #006b24; box-shadow: 0 4px 8px rgba(0, 131, 45, 0.3); }");
        html.append(".link-text { text-align: center; font-size: 13px; color: #6c757d; margin-top: 16px; }");
        html.append(".link-code { display: inline-block; background: #f8f9fa; border: 1px solid #e9ecef; ");
        html.append("padding: 8px 12px; border-radius: 4px; font-family: 'Courier New', monospace; ");
        html.append("font-size: 12px; color: #495057; word-break: break-all; margin-top: 8px; }");
        html.append(".notes-section { background: #fff9e6; border-left: 4px solid #ffc107; ");
        html.append("padding: 20px; border-radius: 4px; margin: 30px 0; }");
        html.append(".notes-section strong { font-size: 14px; color: #856404; display: block; margin-bottom: 8px; }");
        html.append(".notes-section p { font-size: 14px; color: #856404; margin: 0; white-space: pre-wrap; line-height: 1.6; }");
        html.append(".closing { font-size: 15px; color: #4a4a4a; margin-top: 32px; }");
        html.append(".signature { margin-top: 24px; }");
        html.append(".signature-name { font-size: 15px; font-weight: 600; color: #1a1a1a; margin: 4px 0; }");
        html.append(".signature-title { font-size: 13px; color: #6c757d; }");
        html.append(".footer { background: #f8f9fa; padding: 24px 30px; text-align: center; border-top: 1px solid #e9ecef; }");
        html.append(".footer-text { font-size: 12px; color: #6c757d; margin: 0; line-height: 1.6; }");
        html.append(".footer-company { font-size: 13px; font-weight: 600; color: #1a1a1a; margin-bottom: 4px; }");
        html.append("@media only screen and (max-width: 600px) { ");
        html.append(".content { padding: 30px 20px; } ");
        html.append(".header { padding: 30px 20px; } ");
        html.append(".footer { padding: 20px; } ");
        html.append("} ");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class=\"email-wrapper\">");
        html.append("<div class=\"header\">");
        html.append("<h1>Interview Invitation</h1>");
        html.append("<p class=\"subtitle\">Codavert - Professional Software Development</p>");
        html.append("</div>");
        html.append("<div class=\"content\">");
        html.append("<p class=\"greeting\">Dear ").append(escapeHtml(candidateName)).append(",</p>");
        html.append("<p class=\"intro-text\">Thank you for your interest in the <strong>").append(escapeHtml(position));
        html.append("</strong> position at Codavert. We have reviewed your application and are impressed with your qualifications.</p>");
        html.append("<p class=\"intro-text\">We would like to invite you to participate in an interview to further discuss your background and explore how you might contribute to our team.</p>");
        html.append("<div class=\"details-card\">");
        html.append("<div class=\"detail-row\">");
        html.append("<div class=\"detail-label\">Date</div>");
        html.append("<div class=\"detail-value\">").append(escapeHtml(interviewDate)).append("</div>");
        html.append("</div>");
        html.append("<div class=\"detail-row\">");
        html.append("<div class=\"detail-label\">Time</div>");
        html.append("<div class=\"detail-value\">").append(escapeHtml(interviewTime)).append("</div>");
        html.append("</div>");
        html.append("<div class=\"detail-row\">");
        html.append("<div class=\"detail-label\">Location</div>");
        html.append("<div class=\"detail-value\">Google Meet (Online)</div>");
        html.append("</div>");
        html.append("</div>");
        if (meetLink != null && !meetLink.isEmpty()) {
            html.append("<div class=\"meet-button-container\">");
            html.append("<a href=\"").append(escapeHtml(meetLink)).append("\" class=\"meet-button\">");
            html.append("Join Google Meet</a>");
            html.append("<p class=\"link-text\">Or copy this link:</p>");
            html.append("<div class=\"link-code\">").append(escapeHtml(meetLink)).append("</div>");
            html.append("</div>");
        }
        if (notes != null && !notes.isEmpty()) {
            html.append("<div class=\"notes-section\">");
            html.append("<strong>Additional Information</strong>");
            html.append("<p>").append(escapeHtml(notes)).append("</p>");
            html.append("</div>");
        }
        html.append("<p class=\"closing\">We look forward to the opportunity to speak with you and learn more about your experience and career goals.</p>");
        html.append("<div class=\"signature\">");
        html.append("<div class=\"signature-name\">Best regards,</div>");
        html.append("<div class=\"signature-name\">Codavert Recruitment Team</div>");
        html.append("<div class=\"signature-title\">Human Resources</div>");
        html.append("</div>");
        html.append("</div>");
        html.append("<div class=\"footer\">");
        html.append("<p class=\"footer-company\">Codavert</p>");
        html.append("<p class=\"footer-text\">This is an automated email. Please do not reply directly to this message.</p>");
        html.append("<p class=\"footer-text\">If you have any questions or need to reschedule, please contact us at your earliest convenience.</p>");
        html.append("</div>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }
    
    /**
     * Send employee account creation email
     */
    @Async
    public void sendEmployeeAccountCreationEmail(com.codavert.entity.User employeeUser, com.codavert.entity.JobApplication application) {
        if (!isEmailConfigured()) {
            logger.warn("Skipping employee account creation email (not configured) for: {}", employeeUser.getEmail());
            return;
        }
        
        try {
            String subject = "Welcome to Codavert - Your Employee Account Has Been Created";
            String htmlContent = buildEmployeeAccountCreationEmail(employeeUser, application);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(employeeUser.getEmail());
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            logger.info("✅ Employee account creation email sent successfully to {}", employeeUser.getEmail());
            
        } catch (MessagingException e) {
            logger.error("Failed to send employee account creation email to {}: {}", employeeUser.getEmail(), e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected failure while sending employee account creation email to {}: {}", employeeUser.getEmail(), e.getMessage(), e);
        }
    }
    
    /**
     * Build HTML email for employee account creation
     */
    private String buildEmployeeAccountCreationEmail(com.codavert.entity.User employeeUser, com.codavert.entity.JobApplication application) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        html.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }");
        html.append(".header { background: linear-gradient(135deg, #8b45ff 0%, #00d4ff 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }");
        html.append(".content { background: #f8f9fa; padding: 30px; border-radius: 0 0 10px 10px; }");
        html.append(".info-box { background: white; padding: 20px; border-radius: 8px; margin: 15px 0; border-left: 4px solid #8b45ff; }");
        html.append(".credentials { background: #fff3cd; padding: 20px; border-radius: 8px; margin: 20px 0; border: 2px solid #ffc107; }");
        html.append(".credentials strong { color: #856404; }");
        html.append(".button { display: inline-block; background: #8b45ff; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; }");
        html.append(".footer { text-align: center; margin-top: 30px; color: #666; font-size: 12px; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class=\"container\">");
        html.append("<div class=\"header\">");
        html.append("<h1>Welcome to Codavert!</h1>");
        html.append("<p>Your Employee Account Has Been Created</p>");
        html.append("</div>");
        html.append("<div class=\"content\">");
        html.append("<p>Dear ").append(escapeHtml(employeeUser.getFirstName())).append(",</p>");
        html.append("<p>Congratulations! We are thrilled to welcome you to the Codavert team.</p>");
        html.append("<p>Your employee account has been successfully created. Below are your login credentials:</p>");
        
        html.append("<div class=\"credentials\">");
        html.append("<h3 style=\"margin-top: 0; color: #856404;\">🔐 Your Login Credentials</h3>");
        html.append("<p><strong>Username:</strong> ").append(escapeHtml(employeeUser.getEmail())).append("</p>");
        html.append("<p><strong>Password:</strong> 1234</p>");
        html.append("<p style=\"color: #856404; font-size: 14px;\"><em>⚠️ Please change your password after your first login for security purposes.</em></p>");
        html.append("</div>");
        
        html.append("<div class=\"info-box\">");
        html.append("<h3 style=\"margin-top: 0;\">📋 Your Details</h3>");
        html.append("<p><strong>Position:</strong> ").append(escapeHtml(application.getAssignedRole() != null ? application.getAssignedRole() : application.getPosition())).append("</p>");
        if (application.getDepartment() != null) {
            html.append("<p><strong>Department:</strong> ").append(escapeHtml(application.getDepartment())).append("</p>");
        }
        if (application.getJoiningDate() != null) {
            html.append("<p><strong>Joining Date:</strong> ").append(escapeHtml(application.getJoiningDate())).append("</p>");
        }
        if (application.getWorkLocation() != null) {
            html.append("<p><strong>Work Location:</strong> ").append(escapeHtml(application.getWorkLocation())).append("</p>");
        }
        html.append("</div>");
        
        html.append("<p>You can now access the employee portal to:</p>");
        html.append("<ul>");
        html.append("<li>View tasks assigned to you</li>");
        html.append("<li>Track your work progress</li>");
        html.append("<li>Update your profile</li>");
        html.append("<li>Access important company resources</li>");
        html.append("</ul>");
        
        html.append("<p style=\"text-align: center;\">");
        html.append("<a href=\"#\" class=\"button\">Access Employee Portal</a>");
        html.append("</p>");
        
        html.append("<p>If you have any questions or need assistance, please don't hesitate to contact us.</p>");
        html.append("<p>Once again, welcome to the team!</p>");
        html.append("<p>Best regards,<br><strong>Codavert Team</strong></p>");
        html.append("</div>");
        html.append("<div class=\"footer\">");
        html.append("<p>This is an automated email from Codavert.</p>");
        html.append("<p>Please do not reply to this email.</p>");
        html.append("</div>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }
}

