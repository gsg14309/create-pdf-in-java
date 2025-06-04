package com.example.demo.controller;

import com.example.demo.dto.ReportData;
import com.example.demo.dto.ReportItem;
import com.example.demo.service.IPdfGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reports")
public class JasperReportController {

    private final IPdfGenerator pdfGenerator;

    private static final String OUTPUT_FILENAME = "report.pdf";

    public JasperReportController(@Qualifier("jasperPdfService") IPdfGenerator pdfGenerator) {
        this.pdfGenerator = pdfGenerator;
    }

    /**
     * Generate PDF report and return as file download
     * @return PDF file as download
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> generatePdfReport() {
        try {
            // Create sample data
            ReportData reportData = new ReportData();
            reportData.setReportId("SAMPLE-001");
            reportData.setTitle("Sample Report");
            
            List<ReportItem> items = new ArrayList<>();
            ReportItem item1 = new ReportItem();
            item1.setName("Item 1");
            item1.setDescription("Description 1");
            item1.setValue("Value 1");
            items.add(item1);
            
            ReportItem item2 = new ReportItem();
            item2.setName("Item 2");
            item2.setDescription("Description 2");
            item2.setValue("Value 2");
            items.add(item2);
            
            reportData.setItems(items);
            reportData.setData(new HashMap<>());

            // Generate PDF
            String pdfPath = pdfGenerator.generatePdf(reportData);
            Path outputPath = Path.of(pdfPath);

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


    @PostMapping("/generate")
    public ResponseEntity<Resource> generateCustomReport(@RequestBody ReportData reportData) {
        try {
            // Generate PDF
            String pdfPath = pdfGenerator.generatePdf(reportData);


            return createPdfResponse(pdfPath);
        } catch (Exception e) {
            log.error("Error generating PDF report: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private ResponseEntity<Resource> createPdfResponse(String outputPath) {
        File file = new File(outputPath);
        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=" + OUTPUT_FILENAME)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
} 