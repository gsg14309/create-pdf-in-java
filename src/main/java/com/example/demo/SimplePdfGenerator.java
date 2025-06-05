package com.example.demo;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class SimplePdfGenerator {
    
    private static final String TEMPLATE_NAME = "large-report.ftl";
    
    public static void main(String[] args) {
        try {
            // Create output file
            String outputPath = "large-pdf-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".pdf";
            System.out.println("Generating PDF: " + outputPath);
            
            // Create renderer
            ITextRenderer renderer = new ITextRenderer();
            
            // Start timing
            long startTime = System.currentTimeMillis();
            
            // Generate HTML content using FreeMarker template
            String htmlContent = generateHtmlContent(500);
            
            // Set document and layout
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            
            // Create PDF
            try (FileOutputStream os = new FileOutputStream(outputPath)) {
                renderer.createPDF(os, true);
            }
            
            // Calculate and print execution time
            long endTime = System.currentTimeMillis();
            System.out.println("PDF generation completed in " + (endTime - startTime) + "ms");
            System.out.println("Output file: " + outputPath);
            
        } catch (Exception e) {
            System.err.println("Error generating PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String generateHtmlContent(int numberOfPages) throws IOException, TemplateException {
        // Create FreeMarker configuration
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setClassLoaderForTemplateLoading(SimplePdfGenerator.class.getClassLoader(), "templates");
        cfg.setDefaultEncoding("UTF-8");
        
        // Load template
        Template template = cfg.getTemplate(TEMPLATE_NAME);
        
        // Create data model
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("numberOfPages", numberOfPages);
        
        // Process template
        try (StringWriter writer = new StringWriter()) {
            template.process(dataModel, writer);
            return writer.toString();
        }
    }
} 