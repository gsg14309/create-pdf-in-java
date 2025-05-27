package com.example.demo.service;

import com.example.demo.entity.Report;
import com.example.demo.entity.ReportStatus;

import java.util.List;
import java.util.Optional;

public interface ReportService {
    Report createReport(Report report);
    
    Report updateReport(String reportId, Report report);
    
    Optional<Report> getReport(String reportId);
    
    List<Report> getAllReports();
    
    List<Report> getReportsByStatus(ReportStatus status);
    
    List<Report> getReportsByCreator(String createdBy);
    
    void deleteReport(String reportId);
    
    Report updateReportStatus(String reportId, ReportStatus status);
    
    List<Report> searchReportsByTitle(String title);
} 