package com.codavert.service;

import com.codavert.dto.ProposalDto;
import com.codavert.entity.Client;
import com.codavert.entity.Proposal;
import com.codavert.entity.Project;
import com.codavert.entity.User;
import com.codavert.repository.ClientRepository;
import com.codavert.repository.ProposalRepository;
import com.codavert.repository.ProjectRepository;
import com.codavert.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class ProposalService {

    @Autowired private ProposalRepository proposalRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private ClientRepository clientRepository;
    @Autowired private UserRepository userRepository;

    public Page<Proposal> getProposalsByUserId(Long userId, Pageable pageable) {
        return proposalRepository.findByUserId(userId, pageable);
    }

    public Optional<Proposal> getProposalById(Long id) {
        return proposalRepository.findById(id);
    }

    public Proposal createProposal(ProposalDto dto) {
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

        String nextNumber = dto.getProposalNumber() != null ? dto.getProposalNumber() : getNextProposalNumber(user.getId());
        Proposal proposal = new Proposal();
        proposal.setProposalNumber(nextNumber);
        proposal.setProposalDate(dto.getProposalDate() != null ? dto.getProposalDate() : LocalDate.now());
        proposal.setValidUntil(dto.getValidUntil());
        proposal.setProjectTitle(dto.getProjectTitle());
        proposal.setTotalCost(dto.getTotalCost() != null ? dto.getTotalCost() : BigDecimal.ZERO);
        proposal.setPaymentTerms(dto.getPaymentTerms());
        proposal.setStatus(dto.getStatus() != null ? Proposal.ProposalStatus.valueOf(dto.getStatus()) : Proposal.ProposalStatus.DRAFT);
        proposal.setDocumentData(dto.getDocumentData());
        proposal.setProject(project);
        proposal.setClient(client);
        proposal.setUser(user);

        return proposalRepository.save(proposal);
    }

    public Proposal updateProposal(Long id, ProposalDto dto) {
        Proposal proposal = proposalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Proposal not found with id: " + id));

        if (dto.getProposalNumber() != null) {
            proposal.setProposalNumber(dto.getProposalNumber());
        }
        if (dto.getProposalDate() != null) {
            proposal.setProposalDate(dto.getProposalDate());
        }
        if (dto.getValidUntil() != null) {
            proposal.setValidUntil(dto.getValidUntil());
        }
        if (dto.getProjectTitle() != null) {
            proposal.setProjectTitle(dto.getProjectTitle());
        }
        if (dto.getTotalCost() != null) {
            proposal.setTotalCost(dto.getTotalCost());
        }
        if (dto.getPaymentTerms() != null) {
            proposal.setPaymentTerms(dto.getPaymentTerms());
        }
        if (dto.getStatus() != null) {
            proposal.setStatus(Proposal.ProposalStatus.valueOf(dto.getStatus()));
        }
        if (dto.getDocumentData() != null) {
            proposal.setDocumentData(dto.getDocumentData());
        }

        return proposalRepository.save(proposal);
    }

    public void deleteProposal(Long id) {
        Proposal proposal = proposalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Proposal not found with id: " + id));
        proposal.setStatus(Proposal.ProposalStatus.CANCELLED);
        proposalRepository.save(proposal);
    }

    public String getNextProposalNumber(Long userId) {
        String max = proposalRepository.findMaxProposalNumberByUserId(userId);
        int next = 1;
        if (max != null && max.matches("PROP-\\d+")) {
            next = Integer.parseInt(max.substring(5)) + 1;
        }
        return String.format("PROP-%04d", next);
    }
}

