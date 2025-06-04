package com.example.demo.controller;

import com.example.demo.model.ReportData;
import com.example.demo.service.JasperPdfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class JasperReportController {

    private final JasperPdfService jasperPdfService;

    /**
     * Generate PDF report and return as file download
     * @return PDF file as download
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> generatePdfReport() {
        try {
            // Create sample data
            List<ReportData> data = Arrays.asList(
                new ReportData("First item"),
                new ReportData("Second item"),
                new ReportData("Third item")
            );

            // Generate PDF
            Path outputPath = Files.createTempFile("report-", ".pdf");
            jasperPdfService.generatePdf("reports/sample-report.jrxml", data, outputPath);

            // Create resource from file
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(outputPath));

            // Clean up temp file
            Files.delete(outputPath);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (Exception e) {
            log.error("Error generating PDF report: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Generate PDF report and return as byte array
     * @return PDF as byte array
     */
    @GetMapping("/view")
    public ResponseEntity<byte[]> viewPdfReport() {
        try {
            // Create sample data
            List<ReportData> data = Arrays.asList(
                new ReportData("First item"),
                new ReportData("Second item"),
                new ReportData("Third item")
            );

            // Generate PDF bytes
            byte[] pdfBytes = jasperPdfService.generatePdfBytes("reports/sample-report.jrxml", data);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("Error generating PDF report: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Generate PDF report with custom data
     * @param data List of report data
     * @return PDF as byte array
     */
    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateCustomReport(@RequestBody List<ReportData> data) {
        try {
            // Generate PDF bytes
            byte[] pdfBytes = jasperPdfService.generatePdfBytes("reports/sample-report.jrxml", data);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("Error generating PDF report: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 