package com.codavert.repository;

import com.codavert.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    List<Client> findByUserId(Long userId);
    
    Page<Client> findByUserId(Long userId, Pageable pageable);
    
    @Query("SELECT c FROM Client c WHERE c.user.id = :userId AND c.status = :status")
    Page<Client> findByUserIdAndStatus(@Param("userId") Long userId, 
                                      @Param("status") Client.ClientStatus status, 
                                      Pageable pageable);
    
    @Query("SELECT c FROM Client c WHERE c.user.id = :userId AND c.type = :type")
    Page<Client> findByUserIdAndType(@Param("userId") Long userId, 
                                    @Param("type") Client.ClientType type, 
                                    Pageable pageable);
    
    @Query("SELECT c FROM Client c WHERE c.user.id = :userId AND " +
           "(LOWER(c.companyName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.contactPerson) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Client> searchClientsByUserId(@Param("userId") Long userId, 
                                       @Param("searchTerm") String searchTerm, 
                                       Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM Client c WHERE c.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(c) FROM Client c WHERE c.user.id = :userId AND c.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, 
                               @Param("status") Client.ClientStatus status);
}
