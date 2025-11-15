package com.codavert.service;

import com.codavert.dto.JobPostingDto;
import com.codavert.entity.JobPosting;
import com.codavert.repository.JobPostingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class JobPostingService {
    
    @Autowired
    private JobPostingRepository jobPostingRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    public List<JobPostingDto> getAllJobPostings() {
        return jobPostingRepository.findAll().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    public List<JobPostingDto> getActiveJobPostings() {
        return jobPostingRepository.findByStatusOrderByCreatedAtDesc(JobPosting.PostingStatus.ACTIVE)
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    public JobPostingDto getJobPostingById(Long id) {
        JobPosting posting = jobPostingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Job posting not found with id: " + id));
        return convertToDto(posting);
    }
    
    public JobPostingDto createJobPosting(JobPostingDto dto) {
        JobPosting posting = new JobPosting();
        posting.setTitle(dto.getTitle());
        posting.setType(dto.getType() != null ? dto.getType() : JobPosting.JobType.FULL_TIME);
        posting.setLocation(dto.getLocation());
        posting.setExperience(dto.getExperience());
        posting.setSalary(dto.getSalary());
        posting.setDescription(dto.getDescription());
        posting.setRequirements(dto.getRequirements() != null ? dto.getRequirements() : new ArrayList<>());
        posting.setResponsibilities(dto.getResponsibilities() != null ? dto.getResponsibilities() : new ArrayList<>());
        posting.setStatus(dto.getStatus() != null ? dto.getStatus() : JobPosting.PostingStatus.ACTIVE);
        posting.setCreatedAt(LocalDateTime.now());
        posting.setUpdatedAt(LocalDateTime.now());
        
        JobPosting saved = jobPostingRepository.save(posting);
        return convertToDto(saved);
    }
    
    public JobPostingDto updateJobPosting(Long id, JobPostingDto dto) {
        JobPosting posting = jobPostingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Job posting not found with id: " + id));
        
        if (dto.getTitle() != null) posting.setTitle(dto.getTitle());
        if (dto.getType() != null) posting.setType(dto.getType());
        if (dto.getLocation() != null) posting.setLocation(dto.getLocation());
        if (dto.getExperience() != null) posting.setExperience(dto.getExperience());
        if (dto.getSalary() != null) posting.setSalary(dto.getSalary());
        if (dto.getDescription() != null) posting.setDescription(dto.getDescription());
        if (dto.getRequirements() != null) posting.setRequirements(dto.getRequirements());
        if (dto.getResponsibilities() != null) posting.setResponsibilities(dto.getResponsibilities());
        if (dto.getStatus() != null) posting.setStatus(dto.getStatus());
        posting.setUpdatedAt(LocalDateTime.now());
        
        JobPosting saved = jobPostingRepository.save(posting);
        return convertToDto(saved);
    }
    
    public void deleteJobPosting(Long id) {
        if (!jobPostingRepository.existsById(id)) {
            throw new RuntimeException("Job posting not found with id: " + id);
        }
        jobPostingRepository.deleteById(id);
    }
    
    private JobPostingDto convertToDto(JobPosting posting) {
        JobPostingDto dto = new JobPostingDto();
        dto.setId(posting.getId());
        dto.setTitle(posting.getTitle());
        dto.setType(posting.getType());
        dto.setLocation(posting.getLocation());
        dto.setExperience(posting.getExperience());
        dto.setSalary(posting.getSalary());
        dto.setDescription(posting.getDescription());
        dto.setRequirements(posting.getRequirements());
        dto.setResponsibilities(posting.getResponsibilities());
        dto.setStatus(posting.getStatus());
        dto.setCreatedAt(posting.getCreatedAt().format(DATE_FORMATTER));
        dto.setUpdatedAt(posting.getUpdatedAt().format(DATE_FORMATTER));
        return dto;
    }
}

