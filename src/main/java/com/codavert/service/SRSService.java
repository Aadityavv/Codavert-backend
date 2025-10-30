package com.codavert.service;

import com.codavert.dto.SRSDto;
import com.codavert.entity.Client;
import com.codavert.entity.SRS;
import com.codavert.entity.Project;
import com.codavert.entity.User;
import com.codavert.repository.ClientRepository;
import com.codavert.repository.SRSRepository;
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
public class SRSService {

    @Autowired private SRSRepository srsRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private ClientRepository clientRepository;
    @Autowired private UserRepository userRepository;

    public Page<SRS> getSRSsByUserId(Long userId, Pageable pageable) {
        return srsRepository.findByUserId(userId, pageable);
    }

    public Optional<SRS> getSRSById(Long id) {
        return srsRepository.findById(id);
    }

    public SRS createSRS(SRSDto dto) {
        Client client = clientRepository.findById(dto.getClientId())
            .orElseThrow(() -> new RuntimeException("Client not found with id: " + dto.getClientId()));
        
        User user;
        if (dto.getUserId() != null) {
            user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));
        } else if (dto.getProjectId() != null) {
            Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + dto.getProjectId()));
            user = project.getUser();
        } else {
            throw new RuntimeException("Either userId or projectId must be provided");
        }

        Project project = null;
        if (dto.getProjectId() != null) {
            project = projectRepository.findById(dto.getProjectId()).orElse(null);
        }

        String nextNumber = dto.getSrsNumber() != null ? dto.getSrsNumber() : getNextSrsNumber(user.getId());
        SRS srs = new SRS();
        srs.setSrsNumber(nextNumber);
        srs.setSrsDate(dto.getSrsDate() != null ? dto.getSrsDate() : LocalDate.now());
        srs.setVersion(dto.getVersion() != null ? dto.getVersion() : "1.0");
        srs.setProjectName(dto.getProjectName());
        srs.setProjectManager(dto.getProjectManager());
        srs.setStatus(dto.getStatus() != null ? SRS.SRSStatus.valueOf(dto.getStatus()) : SRS.SRSStatus.DRAFT);
        srs.setDocumentData(dto.getDocumentData());
        srs.setProject(project);
        srs.setClient(client);
        srs.setUser(user);

        return srsRepository.save(srs);
    }

    public SRS updateSRS(Long id, SRSDto dto) {
        SRS srs = srsRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("SRS not found with id: " + id));

        if (dto.getSrsNumber() != null) {
            srs.setSrsNumber(dto.getSrsNumber());
        }
        if (dto.getSrsDate() != null) {
            srs.setSrsDate(dto.getSrsDate());
        }
        if (dto.getVersion() != null) {
            srs.setVersion(dto.getVersion());
        }
        if (dto.getProjectName() != null) {
            srs.setProjectName(dto.getProjectName());
        }
        if (dto.getProjectManager() != null) {
            srs.setProjectManager(dto.getProjectManager());
        }
        if (dto.getStatus() != null) {
            srs.setStatus(SRS.SRSStatus.valueOf(dto.getStatus()));
        }
        if (dto.getDocumentData() != null) {
            srs.setDocumentData(dto.getDocumentData());
        }

        return srsRepository.save(srs);
    }

    public void deleteSRS(Long id) {
        SRS srs = srsRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("SRS not found with id: " + id));
        srs.setStatus(SRS.SRSStatus.CANCELLED);
        srsRepository.save(srs);
    }

    public String getNextSrsNumber(Long userId) {
        String max = srsRepository.findMaxSrsNumberByUserId(userId);
        int next = 1;
        if (max != null && max.matches("SRS-\\d+")) {
            next = Integer.parseInt(max.substring(4)) + 1;
        }
        return String.format("SRS-%04d", next);
    }

    public List<SRS> getSRSsByProjectId(Long projectId) {
        return srsRepository.findByProjectId(projectId);
    }
}

