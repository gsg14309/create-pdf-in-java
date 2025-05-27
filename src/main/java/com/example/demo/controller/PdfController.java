package com.example.demo.controller;

import com.example.demo.dto.ReportData;
import com.example.demo.exception.PDFGenerationException;
import com.example.demo.service.PdfGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.io.File;

@Slf4j
@RestController
@RequestMapping("/api/pdf")
@RequiredArgsConstructor
public class PdfController {


    private static final String OUTPUT_FILENAME = "report.pdf";

    private final PdfGeneratorService pdfGeneratorService;

    @PostMapping("/generate")
    public ResponseEntity<Resource> generatePdf(@Valid @RequestBody ReportData reportData) {
        log.debug("Generating PDF for report data: {}", reportData);



        try {
            String outputPath = pdfGeneratorService.generatePdfForBasicReport(reportData);
            return createPdfResponse(outputPath);
        } catch (Exception e) {
            log.error("Failed to generate PDF: {}", e.getMessage(), e);
            throw new PDFGenerationException("Failed to generate PDF", e);
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