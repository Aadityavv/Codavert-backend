package com.codavert.repository;

import com.codavert.entity.Proposal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    
    Optional<Proposal> findByProposalNumber(String proposalNumber);
    
    List<Proposal> findByUserId(Long userId);
    
    Page<Proposal> findByUserId(Long userId, Pageable pageable);
    
    List<Proposal> findByClientId(Long clientId);
    
    Page<Proposal> findByClientId(Long clientId, Pageable pageable);
    
    List<Proposal> findByProjectId(Long projectId);
    
    Page<Proposal> findByProjectId(Long projectId, Pageable pageable);
    
    @Query("SELECT p FROM Proposal p WHERE p.user.id = :userId AND p.status = :status")
    Page<Proposal> findByUserIdAndStatus(@Param("userId") Long userId, 
                                        @Param("status") Proposal.ProposalStatus status, 
                                        Pageable pageable);
    
    @Query("SELECT MAX(p.proposalNumber) FROM Proposal p WHERE p.user.id = :userId")
    String findMaxProposalNumberByUserId(@Param("userId") Long userId);
}

