package com.example.demo.service.impl;

import com.example.demo.entity.Report;
import com.example.demo.entity.ReportStatus;
import com.example.demo.repository.ReportRepository;
import com.example.demo.service.ReportService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    @Override
    public Report createReport(Report report) {
        if (report.getReportId() == null) {
            report.setReportId(UUID.randomUUID().toString());
        }
        report.setStatus(ReportStatus.DRAFT);
        return reportRepository.save(report);
    }

    @Override
    public Report updateReport(String reportId, Report updatedReport) {
        Report existingReport = reportRepository.findByReportId(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found with id: " + reportId));

        existingReport.setTitle(updatedReport.getTitle());
        existingReport.setUpdatedBy(updatedReport.getUpdatedBy());
        
        return reportRepository.save(existingReport);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Report> getReport(String reportId) {
        return reportRepository.findByReportId(reportId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Report> getReportsByStatus(ReportStatus status) {
        return reportRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Report> getReportsByCreator(String createdBy) {
        return reportRepository.findByCreatedBy(createdBy);
    }

    @Override
    public void deleteReport(String reportId) {
        Report report = reportRepository.findByReportId(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found with id: " + reportId));
        reportRepository.delete(report);
    }

    @Override
    public Report updateReportStatus(String reportId, ReportStatus status) {
        Report report = reportRepository.findByReportId(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found with id: " + reportId));
        report.setStatus(status);
        return reportRepository.save(report);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Report> searchReportsByTitle(String title) {
        return reportRepository.findByTitleContainingIgnoreCase(title);
    }
} 