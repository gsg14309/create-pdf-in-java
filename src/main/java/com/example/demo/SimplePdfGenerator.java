package com.example.demo;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SimplePdfGenerator is a standalone utility class for generating PDF documents using FreeMarker templates and Flying Saucer (ITextRenderer).
 * This class provides robust PDF generation capabilities with proper resource management, error handling, and performance optimizations.
 */
@Slf4j
public class SimplePdfGenerator {
    private static final String TEMPLATE_NAME = "large-report.ftl";
    private static final String OUTPUT_DIR = "output";
    private static final int BUFFER_SIZE = 8192;
    private static final int MAX_MEMORY_PDF_SIZE = 50 * 1024 * 1024; // 50MB

    /**
     * Singleton enum for managing PDF generator configuration and resources.
     */
    private enum PdfGeneratorConfig {
        INSTANCE;

        private final Configuration freemarkerConfig;
        private final Map<String, Template> templateCache;

        PdfGeneratorConfig() {
            this.freemarkerConfig = createFreemarkerConfig();
            this.templateCache = new ConcurrentHashMap<>();
            createOutputDirectory();
        }

        private Configuration createFreemarkerConfig() {
            Configuration config = new Configuration(Configuration.VERSION_2_3_31);
            config.setClassLoaderForTemplateLoading(SimplePdfGenerator.class.getClassLoader(), "templates");
            config.setDefaultEncoding("UTF-8");
            return config;
        }

        private void createOutputDirectory() {
            try {
                Files.createDirectories(Paths.get(OUTPUT_DIR));
            } catch (IOException e) {
                log.error("Failed to create output directory: {}", OUTPUT_DIR, e);
            }
        }

        public Configuration getFreemarkerConfig() {
            return freemarkerConfig;
        }

        public Template getTemplate(String templateName) throws IOException {
            return templateCache.computeIfAbsent(templateName, name -> {
                try {
                    return freemarkerConfig.getTemplate(name);
                } catch (IOException e) {
                    throw new PDFGenerationException("Failed to load template: " + name, e);
                }
            });
        }
    }

    /**
     * Entry point for manual testing. Calls PDF generation methods.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            //generateSimplePdf(500);
            generatePdfWithDataList(500);
        } catch (PDFGenerationException e) {
            log.error("PDF generation failed", e);
        }
    }

    /**
     * Generates a large PDF file with a specified number of pages using a FreeMarker template.
     *
     * @param numberOfPages the number of pages to generate
     * @return the path to the generated PDF file
     * @throws PDFGenerationException if PDF generation fails
     */
    public static String generateSimplePdf(int numberOfPages) {
        validateParameters(numberOfPages);
        String outputPath = createOutputPath("large-pdf");
        ITextRenderer renderer = null;
        FileOutputStream os = null;
        
        try {
            log.info("Starting PDF generation with {} pages", numberOfPages);
            long startTime = System.currentTimeMillis();
            
            renderer = new ITextRenderer();
            String htmlContent = generateHtmlContent(numberOfPages);
            
            if (htmlContent.length() > MAX_MEMORY_PDF_SIZE) {
                log.warn("HTML content size ({}) exceeds memory threshold, using file-based processing", 
                    htmlContent.length());
                return generateLargePdf(htmlContent, outputPath);
            }
            
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            
            os = new FileOutputStream(outputPath);
            renderer.createPDF(os, true);
            
            long endTime = System.currentTimeMillis();
            log.info("PDF generation completed in {}ms. Output: {}", (endTime - startTime), outputPath);
            
            return outputPath;
        } catch (Exception e) {
            throw new PDFGenerationException("Failed to generate PDF", e);
        } finally {
            cleanupResources(os, renderer);
        }
    }

    /**
     * Generates a PDF file where each page is based on a ReportData object in a list.
     *
     * @param numberOfPages the number of ReportData objects/pages to generate
     * @return the path to the generated PDF file
     * @throws PDFGenerationException if PDF generation fails
     */
    public static String generatePdfWithDataList(int numberOfPages) {
        validateParameters(numberOfPages);
        String outputPath = createOutputPath("reportdata-list");
        ITextRenderer renderer = null;
        FileOutputStream os = null;
        
        try {
            log.info("Starting PDF generation with {} data items", numberOfPages);
            long startTime = System.currentTimeMillis();
            
            renderer = new ITextRenderer();
            String htmlContent = generateHtmlForReportDataList(numberOfPages);
            
            if (htmlContent.length() > MAX_MEMORY_PDF_SIZE) {
                log.warn("HTML content size ({}) exceeds memory threshold, using file-based processing", 
                    htmlContent.length());
                return generateLargePdf(htmlContent, outputPath);
            }
            
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            
            os = new FileOutputStream(outputPath);
            renderer.createPDF(os, true);
            
            long endTime = System.currentTimeMillis();
            log.info("PDF generation completed in {}ms. Output: {}", (endTime - startTime), outputPath);
            
            return outputPath;
        } catch (Exception e) {
            throw new PDFGenerationException("Failed to generate PDF with data list", e);
        } finally {
            cleanupResources(os, renderer);
        }
    }

    private static String generateHtmlContent(int numberOfPages) throws IOException, TemplateException {
        Template template = PdfGeneratorConfig.INSTANCE.getTemplate(TEMPLATE_NAME);
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("numberOfPages", numberOfPages);
        
        try (StringWriter writer = new StringWriter(BUFFER_SIZE)) {
            template.process(dataModel, writer);
            return writer.toString();
        }
    }

    private static String generateHtmlForReportDataList(int numberOfPages) throws IOException, TemplateException {
        Template template = PdfGeneratorConfig.INSTANCE.getTemplate("reportdata-list.ftl");
        
        java.util.List<com.example.demo.model.ReportData> reportDataList = new java.util.ArrayList<>();
        for (int i = 1; i <= numberOfPages; i++) {
            reportDataList.add(new com.example.demo.model.ReportData("This is the content for page " + i));
        }

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("reportDataList", reportDataList);

        try (StringWriter writer = new StringWriter(BUFFER_SIZE)) {
            template.process(dataModel, writer);
            return writer.toString();
        }
    }

    private static String generateLargePdf(String htmlContent, String outputPath) throws IOException {
        File tempHtmlFile = File.createTempFile("temp_", ".html");
        try {
            // Write HTML content to temporary file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempHtmlFile), BUFFER_SIZE)) {
                writer.write(htmlContent);
            }

            // Generate PDF from file
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocument(tempHtmlFile);
            renderer.layout();

            try (FileOutputStream os = new FileOutputStream(outputPath)) {
                renderer.createPDF(os, true);
            }

            return outputPath;
        } finally {
            if (tempHtmlFile.exists()) {
                tempHtmlFile.delete();
            }
        }
    }

    private static void validateParameters(int numberOfPages) {
        if (numberOfPages <= 0) {
            throw new IllegalArgumentException("Number of pages must be positive");
        }
    }

    private static String createOutputPath(String prefix) {
        return Paths.get(OUTPUT_DIR, 
            prefix + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".pdf")
            .toString();
    }

    private static void cleanupResources(FileOutputStream os, ITextRenderer renderer) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                log.error("Failed to close output stream", e);
            }
        }
        if (renderer != null) {
            renderer.finishPDF();
        }
    }
}

/**
 * Custom exception for PDF generation errors.
 */
class PDFGenerationException extends RuntimeException {
    public PDFGenerationException(String message) {
        super(message);
    }

    public PDFGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
} 