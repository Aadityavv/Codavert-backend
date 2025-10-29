package com.codavert.service;

import com.codavert.dto.MOUDto;
import com.codavert.entity.Client;
import com.codavert.entity.MOU;
import com.codavert.entity.Project;
import com.codavert.entity.User;
import com.codavert.repository.ClientRepository;
import com.codavert.repository.MOURepository;
import com.codavert.repository.ProjectRepository;
import com.codavert.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class MOUService {

    @Autowired private MOURepository mouRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private ClientRepository clientRepository;
    @Autowired private UserRepository userRepository;

    public Page<MOU> getMOUsByUserId(Long userId, Pageable pageable) {
        return mouRepository.findByUserId(userId, pageable);
    }

    public Optional<MOU> getMOUById(Long id) {
        return mouRepository.findById(id);
    }

    public MOU createMOU(MOUDto dto) {
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

        String nextNumber = dto.getMouNumber() != null ? dto.getMouNumber() : getNextMouNumber(user.getId());
        MOU mou = new MOU();
        mou.setMouNumber(nextNumber);
        mou.setEffectiveDate(dto.getEffectiveDate() != null ? dto.getEffectiveDate() : LocalDate.now());
        mou.setStartDate(dto.getStartDate());
        mou.setProjectTitle(dto.getProjectTitle());
        mou.setProjectDescription(dto.getProjectDescription());
        mou.setDuration(dto.getDuration());
        mou.setBudget(dto.getBudget());
        mou.setPartyASignatory(dto.getPartyASignatory());
        mou.setPartyBSignatory(dto.getPartyBSignatory());
        mou.setStatus(dto.getStatus() != null ? MOU.MOUStatus.valueOf(dto.getStatus()) : MOU.MOUStatus.DRAFT);
        mou.setDocumentData(dto.getDocumentData());
        mou.setProject(project);
        mou.setClient(client);
        mou.setUser(user);

        return mouRepository.save(mou);
    }

    public MOU updateMOU(Long id, MOUDto dto) {
        MOU mou = mouRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("MOU not found with id: " + id));

        if (dto.getMouNumber() != null) {
            mou.setMouNumber(dto.getMouNumber());
        }
        if (dto.getEffectiveDate() != null) {
            mou.setEffectiveDate(dto.getEffectiveDate());
        }
        if (dto.getStartDate() != null) {
            mou.setStartDate(dto.getStartDate());
        }
        if (dto.getProjectTitle() != null) {
            mou.setProjectTitle(dto.getProjectTitle());
        }
        if (dto.getProjectDescription() != null) {
            mou.setProjectDescription(dto.getProjectDescription());
        }
        if (dto.getDuration() != null) {
            mou.setDuration(dto.getDuration());
        }
        if (dto.getBudget() != null) {
            mou.setBudget(dto.getBudget());
        }
        if (dto.getPartyASignatory() != null) {
            mou.setPartyASignatory(dto.getPartyASignatory());
        }
        if (dto.getPartyBSignatory() != null) {
            mou.setPartyBSignatory(dto.getPartyBSignatory());
        }
        if (dto.getStatus() != null) {
            mou.setStatus(MOU.MOUStatus.valueOf(dto.getStatus()));
        }
        if (dto.getDocumentData() != null) {
            mou.setDocumentData(dto.getDocumentData());
        }

        return mouRepository.save(mou);
    }

    public void deleteMOU(Long id) {
        MOU mou = mouRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("MOU not found with id: " + id));
        mou.setStatus(MOU.MOUStatus.CANCELLED);
        mouRepository.save(mou);
    }

    public String getNextMouNumber(Long userId) {
        String max = mouRepository.findMaxMouNumberByUserId(userId);
        int next = 1;
        if (max != null && max.matches("MOU-\\d+")) {
            next = Integer.parseInt(max.substring(4)) + 1;
        }
        return String.format("MOU-%04d", next);
    }
}

