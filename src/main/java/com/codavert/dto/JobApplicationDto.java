package com.codavert.dto;

import com.codavert.entity.JobApplication;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationDto {
    private String fullName;
    private String email;
    private String phone;
    private String position;
    private String coverLetter;
    private String resumeUrl;
    private String portfolioUrl;
    private String linkedinUrl;
    private String githubUrl;
    private Integer yearsOfExperience;
    private String skills;
    private JobApplication.ApplicationStatus status;
    private String adminNotes;
    private String assignedRole;
    private Double stipend;
    private String joiningDate;
    private String employmentType;
    private String department;
    private String workLocation;
}





