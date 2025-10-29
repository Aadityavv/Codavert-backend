package com.codavert.repository;

import com.codavert.entity.SRS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SRSRepository extends JpaRepository<SRS, Long> {
    
    Optional<SRS> findBySrsNumber(String srsNumber);
    
    List<SRS> findByUserId(Long userId);
    
    Page<SRS> findByUserId(Long userId, Pageable pageable);
    
    List<SRS> findByClientId(Long clientId);
    
    Page<SRS> findByClientId(Long clientId, Pageable pageable);
    
    List<SRS> findByProjectId(Long projectId);
    
    Page<SRS> findByProjectId(Long projectId, Pageable pageable);
    
    @Query("SELECT s FROM SRS s WHERE s.user.id = :userId AND s.status = :status")
    Page<SRS> findByUserIdAndStatus(@Param("userId") Long userId, 
                                    @Param("status") SRS.SRSStatus status, 
                                    Pageable pageable);
    
    @Query("SELECT MAX(s.srsNumber) FROM SRS s WHERE s.user.id = :userId")
    String findMaxSrsNumberByUserId(@Param("userId") Long userId);
}

