package com.example.demo.incubation;

import com.example.demo.dto.ReportData;
import com.example.demo.exception.PDFGenerationException;
import com.example.demo.util.FileStorageUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.UUID;

/**
 * Service for generating PDF documents from templates.
 */
//@Service
public class PdfGeneratorService {

    private static final Logger logger = LoggerFactory.getLogger(PdfGeneratorService.class);
    private static final String PDF_EXTENSION = ".pdf";
    private static final String TEMPLATE_DIRECTORY = "templates";

    private final Configuration freemarkerConfig;

    @Autowired
    private FileStorageUtil fileStorageUtil;

    public PdfGeneratorService() {
        logger.info("Initializing PdfGeneratorService");
        freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
        freemarkerConfig.setClassLoaderForTemplateLoading(
            this.getClass().getClassLoader(), TEMPLATE_DIRECTORY);
        freemarkerConfig.setDefaultEncoding("UTF-8");
        logger.debug("FreeMarker configuration initialized");
    }

    /**
     * Generates a PDF document from the specified template and data.
     *
     * @param templateName the name of the template to use
     * @param reportData the data to populate the template with
     * @return the path where the PDF was saved
     * @throws PDFGenerationException if PDF generation fails
     */
    public String generatePdf(String templateName, ReportData reportData) {
        validateInput(templateName, reportData);
        
        logger.info("Starting PDF generation for template: {}", templateName);
        logger.debug("Template data: {}", reportData);

        Template template = getReportTemplate(templateName);
        logger.debug("Template loaded successfully");

        String htmlContent = addDataIntoTemplate(reportData, template);
        logger.debug("Template processed successfully");

        byte[] pdfContent = generatePdfFromContent(htmlContent);
        String savedPath = savePdfToFileSystem(templateName, pdfContent);
        
        logger.info("PDF generation completed. Saved at: {}", savedPath);
        return savedPath;
    }

    private void validateInput(String templateName, ReportData reportData) {
        if (templateName == null || templateName.trim().isEmpty()) {
            throw new IllegalArgumentException("Template name cannot be null or empty");
        }
        if (reportData == null) {
            throw new IllegalArgumentException("Report data cannot be null");
        }
    }

    private String savePdfToFileSystem(String templateName, byte[] pdfContent) {
        try {
            String fileName = String.format("%s_%s%s", templateName, UUID.randomUUID(), PDF_EXTENSION);
            String savedPath = fileStorageUtil.savePdf(pdfContent, fileName);
            logger.info("PDF saved to file system at: {}", savedPath);
            return savedPath;
        } catch (IOException e) {
            logger.error("Failed to save PDF: {}", e.getMessage(), e);
            throw new PDFGenerationException("Failed to save PDF", e);
        }
    }

    private byte[] generatePdfFromContent(String htmlContent) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            logger.debug("Creating PDF in memory");
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream, true);
            return outputStream.toByteArray();
        } catch (IOException e) {
            logger.error("Failed to generate PDF: {}", e.getMessage(), e);
            throw new PDFGenerationException("Failed to generate PDF", e);
        }
    }

    private String addDataIntoTemplate(ReportData reportData, Template template) {
        try (StringWriter stringWriter = new StringWriter()) {
            template.process(reportData, stringWriter);
            return stringWriter.toString();
        } catch (TemplateException | IOException e) {
            logger.error("Failed to process template: {}", e.getMessage(), e);
            throw new PDFGenerationException("Failed to process template", e);
        }
    }

    protected Template getReportTemplate(String templateName) {
        try {
            return freemarkerConfig.getTemplate(templateName);
        } catch (IOException e) {
            logger.error("Failed to load template {}: {}", templateName, e.getMessage(), e);
            throw new PDFGenerationException("Failed to load template: " + templateName, e);
        }
    }
} 