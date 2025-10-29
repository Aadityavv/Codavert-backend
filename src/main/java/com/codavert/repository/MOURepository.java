package com.codavert.repository;

import com.codavert.entity.MOU;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MOURepository extends JpaRepository<MOU, Long> {
    
    Optional<MOU> findByMouNumber(String mouNumber);
    
    List<MOU> findByUserId(Long userId);
    
    Page<MOU> findByUserId(Long userId, Pageable pageable);
    
    List<MOU> findByClientId(Long clientId);
    
    Page<MOU> findByClientId(Long clientId, Pageable pageable);
    
    List<MOU> findByProjectId(Long projectId);
    
    Page<MOU> findByProjectId(Long projectId, Pageable pageable);
    
    @Query("SELECT m FROM MOU m WHERE m.user.id = :userId AND m.status = :status")
    Page<MOU> findByUserIdAndStatus(@Param("userId") Long userId, 
                                   @Param("status") MOU.MOUStatus status, 
                                   Pageable pageable);
    
    @Query("SELECT MAX(m.mouNumber) FROM MOU m WHERE m.user.id = :userId")
    String findMaxMouNumberByUserId(@Param("userId") Long userId);
}

