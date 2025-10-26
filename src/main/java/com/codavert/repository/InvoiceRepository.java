package com.codavert.repository;

import com.codavert.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    
    List<Invoice> findByUserId(Long userId);
    
    Page<Invoice> findByUserId(Long userId, Pageable pageable);
    
    List<Invoice> findByClientId(Long clientId);
    
    Page<Invoice> findByClientId(Long clientId, Pageable pageable);
    
    List<Invoice> findByProjectId(Long projectId);
    
    Page<Invoice> findByProjectId(Long projectId, Pageable pageable);
    
    @Query("SELECT i FROM Invoice i WHERE i.user.id = :userId AND i.status = :status")
    Page<Invoice> findByUserIdAndStatus(@Param("userId") Long userId, 
                                        @Param("status") Invoice.InvoiceStatus status, 
                                        Pageable pageable);
    
    @Query("SELECT i FROM Invoice i WHERE i.user.id = :userId AND i.invoiceDate BETWEEN :startDate AND :endDate")
    List<Invoice> findByUserIdAndDateRange(@Param("userId") Long userId, 
                                          @Param("startDate") LocalDate startDate, 
                                          @Param("endDate") LocalDate endDate);
    
    @Query("SELECT i FROM Invoice i WHERE i.user.id = :userId AND i.dueDate < :date AND i.status != 'PAID'")
    List<Invoice> findOverdueInvoicesByUserId(@Param("userId") Long userId, 
                                             @Param("date") LocalDate date);
    
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.user.id = :userId AND i.status = :status")
    Double sumTotalAmountByUserIdAndStatus(@Param("userId") Long userId, 
                                          @Param("status") Invoice.InvoiceStatus status);
    
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.user.id = :userId AND i.invoiceDate BETWEEN :startDate AND :endDate")
    Double sumTotalAmountByUserIdAndDateRange(@Param("userId") Long userId, 
                                             @Param("startDate") LocalDate startDate, 
                                             @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.user.id = :userId AND i.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, 
                               @Param("status") Invoice.InvoiceStatus status);
    
    @Query("SELECT MAX(i.invoiceNumber) FROM Invoice i WHERE i.user.id = :userId")
    String findMaxInvoiceNumberByUserId(@Param("userId") Long userId);
}
