package com.codavert.service;

import com.codavert.entity.Project;
import com.codavert.entity.ProjectDocument;
import com.codavert.entity.User;
import com.codavert.repository.ProjectDocumentRepository;
import com.codavert.repository.ProjectRepository;
import com.codavert.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {
    
    @Autowired
    private ProjectDocumentRepository documentRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public ProjectDocument createDocument(Long projectId, Long userId, String title, 
                                        String description, ProjectDocument.DocumentType type) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        ProjectDocument document = new ProjectDocument();
        document.setTitle(title);
        document.setDescription(description);
        document.setType(type);
        document.setProject(project);
        document.setUser(user);
        document.setStatus(ProjectDocument.DocumentStatus.DRAFT);
        
        return documentRepository.save(document);
    }
    
    public List<ProjectDocument> getDocumentsByProjectId(Long projectId) {
        return documentRepository.findByProjectId(projectId);
    }
    
    public List<ProjectDocument> getDocumentsByUserId(Long userId) {
        return documentRepository.findByUserId(userId);
    }
    
    public Optional<ProjectDocument> getDocumentById(Long id) {
        return documentRepository.findById(id);
    }
    
    public ProjectDocument updateDocument(Long id, String title, String description, 
                                        String content, ProjectDocument.DocumentStatus status) {
        ProjectDocument document = documentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));
        
        if (title != null) {
            document.setTitle(title);
        }
        if (description != null) {
            document.setDescription(description);
        }
        if (content != null) {
            document.setContent(content);
        }
        if (status != null) {
            document.setStatus(status);
        }
        
        return documentRepository.save(document);
    }
    
    public void deleteDocument(Long id) {
        ProjectDocument document = documentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));
        
        documentRepository.delete(document);
    }
    
    public String generateMOUContent(Project project) {
        StringBuilder content = new StringBuilder();
        content.append("MEMORANDUM OF UNDERSTANDING\n");
        content.append("============================\n\n");
        content.append("Project: ").append(project.getTitle()).append("\n");
        content.append("Client: ").append(project.getClient().getCompanyName()).append("\n");
        content.append("Start Date: ").append(project.getStartDate()).append("\n");
        content.append("End Date: ").append(project.getEndDate()).append("\n");
        content.append("Budget: $").append(project.getBudget()).append("\n\n");
        content.append("Description:\n").append(project.getDescription()).append("\n\n");
        content.append("Requirements:\n").append(project.getRequirements()).append("\n\n");
        content.append("Deliverables:\n").append(project.getDeliverables()).append("\n\n");
        content.append("Technologies: ").append(project.getTechnologies()).append("\n\n");
        content.append("Generated on: ").append(LocalDateTime.now()).append("\n");
        
        return content.toString();
    }
    
    public String generateSRSContent(Project project) {
        StringBuilder content = new StringBuilder();
        content.append("SOFTWARE REQUIREMENTS SPECIFICATION\n");
        content.append("===================================\n\n");
        content.append("Project: ").append(project.getTitle()).append("\n");
        content.append("Client: ").append(project.getClient().getCompanyName()).append("\n");
        content.append("Version: 1.0\n");
        content.append("Date: ").append(LocalDateTime.now()).append("\n\n");
        
        content.append("1. INTRODUCTION\n");
        content.append("===============\n");
        content.append("This document describes the software requirements for the ").append(project.getTitle()).append(" project.\n\n");
        
        content.append("2. PROJECT OVERVIEW\n");
        content.append("===================\n");
        content.append(project.getDescription()).append("\n\n");
        
        content.append("3. FUNCTIONAL REQUIREMENTS\n");
        content.append("===========================\n");
        content.append(project.getRequirements()).append("\n\n");
        
        content.append("4. DELIVERABLES\n");
        content.append("===============\n");
        content.append(project.getDeliverables()).append("\n\n");
        
        content.append("5. TECHNICAL SPECIFICATIONS\n");
        content.append("============================\n");
        content.append("Technologies: ").append(project.getTechnologies()).append("\n");
        content.append("Estimated Hours: ").append(project.getEstimatedHours()).append("\n");
        content.append("Hourly Rate: $").append(project.getHourlyRate()).append("\n\n");
        
        return content.toString();
    }
}
