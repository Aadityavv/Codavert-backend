package com.codavert.controller;

import com.codavert.dto.ProposalDto;
import com.codavert.entity.Proposal;
import com.codavert.service.ProposalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/proposals")
public class ProposalController {

    @Autowired
    private ProposalService proposalService;

    @GetMapping
    public ResponseEntity<Page<Proposal>> getProposals(@RequestParam Long userId, Pageable pageable) {
        return ResponseEntity.ok(proposalService.getProposalsByUserId(userId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProposalById(@PathVariable Long id) {
        return proposalService.getProposalById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Proposal> createProposal(@RequestBody ProposalDto dto) {
        return ResponseEntity.ok(proposalService.createProposal(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proposal> updateProposal(@PathVariable Long id, @RequestBody ProposalDto dto) {
        return ResponseEntity.ok(proposalService.updateProposal(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProposal(@PathVariable Long id) {
        proposalService.deleteProposal(id);
        return ResponseEntity.ok("Proposal deleted successfully");
    }

    @GetMapping("/next-number")
    public ResponseEntity<String> getNextProposalNumber(@RequestParam Long userId) {
        return ResponseEntity.ok(proposalService.getNextProposalNumber(userId));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Proposal>> getProposalsByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(proposalService.getProposalsByProjectId(projectId));
    }
}

