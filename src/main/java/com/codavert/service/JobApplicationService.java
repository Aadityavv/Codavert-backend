package com.codavert.service;

import com.codavert.dto.JobApplicationDto;
import com.codavert.entity.JobApplication;
import com.codavert.entity.User;
import com.codavert.repository.JobApplicationRepository;
import com.codavert.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobApplicationService {
    
    @Autowired
    private JobApplicationRepository jobApplicationRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Create new job application (public endpoint)
    @Transactional
    public JobApplication createApplication(JobApplicationDto dto) {
        // Check if email already exists
        if (jobApplicationRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("An application with this email already exists");
        }
        
        JobApplication application = new JobApplication();
        application.setFullName(dto.getFullName());
        application.setEmail(dto.getEmail());
        application.setPhone(dto.getPhone());
        application.setPosition(dto.getPosition());
        application.setCoverLetter(dto.getCoverLetter());
        application.setResumeUrl(dto.getResumeUrl());
        application.setPortfolioUrl(dto.getPortfolioUrl());
        application.setLinkedinUrl(dto.getLinkedinUrl());
        application.setGithubUrl(dto.getGithubUrl());
        application.setYearsOfExperience(dto.getYearsOfExperience());
        application.setSkills(dto.getSkills());
        application.setStatus(JobApplication.ApplicationStatus.NEW);
        application.setAppliedAt(LocalDateTime.now());
        
        JobApplication saved = jobApplicationRepository.save(application);
        
        // Send confirmation email to applicant
        try {
            sendApplicationConfirmationEmail(saved);
        } catch (Exception e) {
            // Log error but don't fail the application submission
            System.err.println("Failed to send confirmation email: " + e.getMessage());
        }
        
        return saved;
    }
    
    // Get all applications
    public List<JobApplication> getAllApplications() {
        return jobApplicationRepository.findByOrderByAppliedAtDesc();
    }
    
    // Get application by ID
    public JobApplication getApplicationById(Long id) {
        return jobApplicationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Application not found with id: " + id));
    }
    
    // Get applications by status
    public List<JobApplication> getApplicationsByStatus(JobApplication.ApplicationStatus status) {
        return jobApplicationRepository.findByStatusOrderByAppliedAtDesc(status);
    }
    
    // Update application status
    @Transactional
    public JobApplication updateApplicationStatus(Long id, JobApplication.ApplicationStatus status, String adminNotes) {
        JobApplication application = getApplicationById(id);
        application.setStatus(status);
        if (adminNotes != null) {
            application.setAdminNotes(adminNotes);
        }
        
        if (status == JobApplication.ApplicationStatus.REVIEWING || 
            status == JobApplication.ApplicationStatus.SHORTLISTED ||
            status == JobApplication.ApplicationStatus.INTERVIEWED) {
            application.setReviewedAt(LocalDateTime.now());
        }
        
        return jobApplicationRepository.save(application);
    }
    
    // Hire applicant
    @Transactional
    public JobApplication hireApplicant(Long id, JobApplicationDto hireDetails) {
        JobApplication application = getApplicationById(id);
        
        application.setStatus(JobApplication.ApplicationStatus.HIRED);
        application.setAssignedRole(hireDetails.getAssignedRole());
        application.setStipend(hireDetails.getStipend());
        application.setJoiningDate(hireDetails.getJoiningDate());
        application.setEmploymentType(hireDetails.getEmploymentType());
        application.setDepartment(hireDetails.getDepartment());
        application.setWorkLocation(hireDetails.getWorkLocation());
        application.setHiredAt(LocalDateTime.now());
        
        if (hireDetails.getAdminNotes() != null) {
            application.setAdminNotes(hireDetails.getAdminNotes());
        }
        
        JobApplication saved = jobApplicationRepository.save(application);
        
        // Send offer letter email
        try {
            sendOfferLetterEmail(saved);
        } catch (Exception e) {
            System.err.println("Failed to send offer letter email: " + e.getMessage());
        }
        
        return saved;
    }
    
    // Update application
    @Transactional
    public JobApplication updateApplication(Long id, JobApplicationDto dto) {
        JobApplication application = getApplicationById(id);
        
        if (dto.getFullName() != null) application.setFullName(dto.getFullName());
        if (dto.getEmail() != null) application.setEmail(dto.getEmail());
        if (dto.getPhone() != null) application.setPhone(dto.getPhone());
        if (dto.getPosition() != null) application.setPosition(dto.getPosition());
        if (dto.getCoverLetter() != null) application.setCoverLetter(dto.getCoverLetter());
        if (dto.getResumeUrl() != null) application.setResumeUrl(dto.getResumeUrl());
        if (dto.getPortfolioUrl() != null) application.setPortfolioUrl(dto.getPortfolioUrl());
        if (dto.getLinkedinUrl() != null) application.setLinkedinUrl(dto.getLinkedinUrl());
        if (dto.getGithubUrl() != null) application.setGithubUrl(dto.getGithubUrl());
        if (dto.getYearsOfExperience() != null) application.setYearsOfExperience(dto.getYearsOfExperience());
        if (dto.getSkills() != null) application.setSkills(dto.getSkills());
        if (dto.getStatus() != null) application.setStatus(dto.getStatus());
        if (dto.getAdminNotes() != null) application.setAdminNotes(dto.getAdminNotes());
        
        return jobApplicationRepository.save(application);
    }
    
    // Delete application
    @Transactional
    public void deleteApplication(Long id) {
        JobApplication application = getApplicationById(id);
        jobApplicationRepository.delete(application);
    }
    
    // Accept offer and create employee account
    @Transactional
    public User acceptOfferAndCreateStaffAccount(Long applicationId) {
        JobApplication application = getApplicationById(applicationId);
        
        // Check if already accepted
        if (application.getOfferAccepted() != null && application.getOfferAccepted()) {
            throw new RuntimeException("Offer has already been accepted for this application");
        }
        
        // Check if already hired
        if (application.getStatus() != JobApplication.ApplicationStatus.HIRED) {
            throw new RuntimeException("Application must be in HIRED status to accept offer");
        }
        
        // Check if user already exists
        if (userRepository.existsByEmail(application.getEmail())) {
            throw new RuntimeException("A user account with this email already exists");
        }
        
        // Create employee user account
        User employeeUser = new User();
        employeeUser.setUsername(application.getEmail()); // Use email as username
        employeeUser.setEmail(application.getEmail());
        employeeUser.setPassword(passwordEncoder.encode("1234")); // Default password
        employeeUser.setFirstName(application.getFullName().split(" ")[0]);
        employeeUser.setLastName(application.getFullName().split(" ").length > 1 
            ? application.getFullName().substring(application.getFullName().indexOf(" ") + 1) 
            : "");
        employeeUser.setPhone(application.getPhone());
        employeeUser.setRole(User.Role.STAFF);
        employeeUser.setStatus(User.UserStatus.ACTIVE);
        
        User savedUser = userRepository.save(employeeUser);
        
        // Update application
        application.setOfferAccepted(true);
        application.setStatus(JobApplication.ApplicationStatus.OFFER_ACCEPTED);
        application.setStaffUserId(savedUser.getId());
        jobApplicationRepository.save(application);
        
        // Send account creation email
        try {
            emailService.sendEmployeeAccountCreationEmail(savedUser, application);
        } catch (Exception e) {
            System.err.println("Failed to send account creation email: " + e.getMessage());
        }
        
        return savedUser;
    }
    
    // Send application confirmation email
    private void sendApplicationConfirmationEmail(JobApplication application) {
        String subject = "Application Received - " + application.getPosition();
        String body = String.format(
            "Dear %s,\n\n" +
            "Thank you for applying for the %s position at Codavert.\n\n" +
            "We have received your application and will review it shortly. " +
            "If your qualifications match our requirements, we will contact you for the next steps.\n\n" +
            "Application Details:\n" +
            "Position: %s\n" +
            "Applied On: %s\n\n" +
            "Best regards,\n" +
            "Codavert Team",
            application.getFullName(),
            application.getPosition(),
            application.getPosition(),
            application.getAppliedAt().toLocalDate()
        );
        
        emailService.sendEmail(application.getEmail(), subject, body);
    }
    
    // Send offer letter email
    private void sendOfferLetterEmail(JobApplication application) {
        String subject = "Congratulations! Job Offer from Codavert";
        String body = String.format(
            "Dear %s,\n\n" +
            "Congratulations! We are pleased to offer you the position of %s at Codavert.\n\n" +
            "Offer Details:\n" +
            "Position: %s\n" +
            "Department: %s\n" +
            "Employment Type: %s\n" +
            "Stipend/Salary: ₹%.2f\n" +
            "Joining Date: %s\n" +
            "Work Location: %s\n\n" +
            "Please login to the portal to download your official offer letter.\n\n" +
            "We look forward to having you on our team!\n\n" +
            "Best regards,\n" +
            "Codavert Team",
            application.getFullName(),
            application.getAssignedRole(),
            application.getAssignedRole(),
            application.getDepartment() != null ? application.getDepartment() : "Technology",
            application.getEmploymentType() != null ? application.getEmploymentType() : "Full-time",
            application.getStipend(),
            application.getJoiningDate(),
            application.getWorkLocation() != null ? application.getWorkLocation() : "Office"
        );
        
        emailService.sendEmail(application.getEmail(), subject, body);
    }
}





