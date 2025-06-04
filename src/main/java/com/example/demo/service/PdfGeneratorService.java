package com.example.demo.service;

import com.example.demo.dto.ReportData;
import com.example.demo.entity.Report;
import com.example.demo.entity.ReportStatus;
import com.example.demo.exception.PDFGenerationException;
import com.example.demo.repository.ReportRepository;
import com.example.demo.util.FileStorageUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.UUID;

/**
 * Service for generating PDF documents from templates.
 */
@Service
@Slf4j
public class PdfGeneratorService implements IPdfGenerator{

    private static final String PDF_EXTENSION = ".pdf";

    private static final String BASIC_REPORT_TEMPLATE_NAME = "report.ftl";

    private final Configuration freemarkerConfig;
    private final FileStorageUtil fileStorageUtil;
    private final ReportRepository reportRepository;

    // Explicitly define the constructor with @Qualifier to avoid ambiguity with springs own freemarkerConfig bean
    //lombok will not generate the constructor using qualifier
    public PdfGeneratorService( @Qualifier("customFreemarkerConfig") Configuration freemarkerConfig,
                                FileStorageUtil fileStorageUtil,
                                ReportRepository reportRepository)
    {
        this.freemarkerConfig = freemarkerConfig;
        this.fileStorageUtil = fileStorageUtil;
        this.reportRepository = reportRepository;
    }


    /**
     * Generates a PDF document from the specified template and data.
     *
     * @param reportData the data to populate the template with
     * @return the path where the PDF was saved
     * @throws PDFGenerationException if PDF generation fails
     */
    public String generatePdf(ReportData reportData) {

        
        log.info("Starting PDF generation for template: {}", BASIC_REPORT_TEMPLATE_NAME);
        log.debug("Template data: {}", reportData);

        Template template = getReportTemplate(BASIC_REPORT_TEMPLATE_NAME);
        log.debug("Template loaded successfully");

        String htmlContent = addDataIntoTemplate(reportData, template);
        log.debug("Template processed successfully");

        byte[] pdfContent = generatePdfFromContent(htmlContent);
        String savedPath = savePdfToFileSystem(BASIC_REPORT_TEMPLATE_NAME, pdfContent);
        
        // Create and persist the report
        Report report = new Report();
        report.setTitle(reportData.getTitle());
        report.setReportId(UUID.randomUUID().toString());
        report.setStatus(ReportStatus.COMPLETED);
        reportRepository.save(report);
        
        log.info("PDF generation completed. Saved at: {} and report persisted with ID: {}", savedPath, report.getReportId());
        return savedPath;
    }


    private String savePdfToFileSystem(String templateName, byte[] pdfContent) {
        try {
            String fileName = String.format("%s_%s%s", templateName, UUID.randomUUID(), PDF_EXTENSION);
            String savedPath = fileStorageUtil.savePdf(pdfContent, fileName);
            log.info("PDF saved to file system at: {}", savedPath);
            return savedPath;
        } catch (IOException e) {
            log.error("Failed to save PDF: {}", e.getMessage(), e);
            throw new PDFGenerationException("Failed to save PDF", e);
        }
    }

    private byte[] generatePdfFromContent(String htmlContent) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            log.debug("Creating PDF in memory");
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream, true);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Failed to generate PDF: {}", e.getMessage(), e);
            throw new PDFGenerationException("Failed to generate PDF", e);
        }
    }

    private String addDataIntoTemplate(ReportData reportData, Template template) {
        try (StringWriter stringWriter = new StringWriter()) {
            template.process(reportData, stringWriter);
            return stringWriter.toString();
        } catch (TemplateException | IOException e) {
            log.error("Failed to process template: {}", e.getMessage(), e);
            throw new PDFGenerationException("Failed to process template", e);
        }
    }

    protected Template getReportTemplate(String templateName) {
        try {
            return freemarkerConfig.getTemplate(templateName);
        } catch (IOException e) {
            log.error("Failed to load template {}: {}", templateName, e.getMessage(), e);
            throw new PDFGenerationException("Failed to load template: " + templateName, e);
        }
    }
} 