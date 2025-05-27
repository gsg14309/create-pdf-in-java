package com.example.demo.repository;

import com.example.demo.entity.Report;
import com.example.demo.entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    Optional<Report> findByReportId(String reportId);
    
    List<Report> findByStatus(ReportStatus status);
    
    List<Report> findByCreatedBy(String createdBy);
    
    boolean existsByReportId(String reportId);
    
    List<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status);
    
    List<Report> findByTitleContainingIgnoreCase(String title);
} 