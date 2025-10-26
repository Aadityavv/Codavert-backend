package com.codavert.service;

import com.codavert.dto.ProjectDto;
import com.codavert.entity.Client;
import com.codavert.entity.Project;
import com.codavert.entity.User;
import com.codavert.repository.ClientRepository;
import com.codavert.repository.ProjectRepository;
import com.codavert.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ClientRepository clientRepository;
    
    public Project createProject(ProjectDto projectDto, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Client client = clientRepository.findById(projectDto.getClientId())
            .orElseThrow(() -> new RuntimeException("Client not found with id: " + projectDto.getClientId()));
        
        Project project = new Project();
        String title = projectDto.getTitle() != null ? projectDto.getTitle() : projectDto.getName();
        project.setTitle(title);
        project.setDescription(projectDto.getDescription());
        project.setStartDate(projectDto.getStartDate());
        project.setEndDate(projectDto.getEndDate());
        project.setEstimatedHours(projectDto.getEstimatedHours());
        project.setActualHours(projectDto.getActualHours());
        project.setBudget(projectDto.getBudget());
        project.setHourlyRate(projectDto.getHourlyRate());
        project.setRequirements(projectDto.getRequirements());
        project.setDeliverables(projectDto.getDeliverables());
        project.setTechnologies(projectDto.getTechnologies());
        project.setUser(user);
        project.setClient(client);
        
        if (projectDto.getStatus() != null) {
            project.setStatus(Project.ProjectStatus.valueOf(projectDto.getStatus()));
        }
        if (projectDto.getType() != null) {
            project.setType(Project.ProjectType.valueOf(projectDto.getType()));
        }
        if (projectDto.getPriority() != null) {
            project.setPriority(Project.Priority.valueOf(projectDto.getPriority()));
        }
        
        return projectRepository.save(project);
    }
    
    public Page<Project> getAllProjectsByUserId(Long userId, Pageable pageable) {
        return projectRepository.findByUserId(userId, pageable);
    }
    
    public List<Project> getAllProjectsByUserId(Long userId) {
        return projectRepository.findByUserId(userId);
    }
    
    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }
    
    public Project updateProject(Long id, ProjectDto projectDto) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        
        String updatedTitle = projectDto.getTitle() != null ? projectDto.getTitle() : projectDto.getName();
        project.setTitle(updatedTitle);
        project.setDescription(projectDto.getDescription());
        project.setStartDate(projectDto.getStartDate());
        project.setEndDate(projectDto.getEndDate());
        project.setEstimatedHours(projectDto.getEstimatedHours());
        project.setActualHours(projectDto.getActualHours());
        project.setBudget(projectDto.getBudget());
        project.setHourlyRate(projectDto.getHourlyRate());
        project.setRequirements(projectDto.getRequirements());
        project.setDeliverables(projectDto.getDeliverables());
        project.setTechnologies(projectDto.getTechnologies());
        
        if (projectDto.getStatus() != null) {
            project.setStatus(Project.ProjectStatus.valueOf(projectDto.getStatus()));
        }
        if (projectDto.getType() != null) {
            project.setType(Project.ProjectType.valueOf(projectDto.getType()));
        }
        if (projectDto.getPriority() != null) {
            project.setPriority(Project.Priority.valueOf(projectDto.getPriority()));
        }
        
        return projectRepository.save(project);
    }
    
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        
        project.setStatus(Project.ProjectStatus.CANCELLED);
        projectRepository.save(project);
    }
    
    public Page<Project> searchProjects(Long userId, String searchTerm, Pageable pageable) {
        return projectRepository.searchProjectsByUserId(userId, searchTerm, pageable);
    }
    
    public Page<Project> getProjectsByStatus(Long userId, String status, Pageable pageable) {
        return projectRepository.findByUserIdAndStatus(userId, Project.ProjectStatus.valueOf(status), pageable);
    }
    
    public Page<Project> getProjectsByType(Long userId, String type, Pageable pageable) {
        return projectRepository.findByUserIdAndType(userId, Project.ProjectType.valueOf(type), pageable);
    }
    
    public List<Project> getOverdueProjects(Long userId) {
        return projectRepository.findOverdueProjects(userId, LocalDate.now(), Pageable.unpaged()).getContent();
    }
    
    public Long getProjectCountByUserId(Long userId) {
        return projectRepository.countByUserId(userId);
    }
    
    public Long getProjectCountByUserIdAndStatus(Long userId, String status) {
        return projectRepository.countByUserIdAndStatus(userId, Project.ProjectStatus.valueOf(status));
    }
    
    public Double getTotalBudgetByUserIdAndStatus(Long userId, String status) {
        Double total = projectRepository.sumBudgetByUserIdAndStatus(userId, Project.ProjectStatus.valueOf(status));
        return total != null ? total : 0.0;
    }
}
