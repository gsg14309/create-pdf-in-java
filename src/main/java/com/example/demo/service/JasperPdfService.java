package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class JasperPdfService {

    /**
     * Generate PDF from a JRXML template and data
     * @param templatePath Path to the JRXML template in the classpath
     * @param data List of data objects to be used in the report
     * @param outputPath Path where the PDF should be saved
     * @return Path to the generated PDF file
     */
    public Path generatePdf(String templatePath, List<?> data, Path outputPath) {
        try {
            // Load the JRXML template
            InputStream templateStream = new ClassPathResource(templatePath).getInputStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);

            // Create data source
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

            // Set parameters
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("REPORT_TITLE", "Generated Report");

            // Fill the report
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Export to PDF
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath.toString());

            return outputPath;
        } catch (JRException | IOException e) {
            log.error("Error generating PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    /**
     * Generate PDF from a JRXML template and data, returning the PDF as a byte array
     * @param templatePath Path to the JRXML template in the classpath
     * @param data List of data objects to be used in the report
     * @return PDF as byte array
     */
    public byte[] generatePdfBytes(String templatePath, List<?> data) {
        try {
            // Load the JRXML template
            InputStream templateStream = new ClassPathResource(templatePath).getInputStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);

            // Create data source
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

            // Set parameters
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("REPORT_TITLE", "Generated Report");

            // Fill the report
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Export to PDF bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            return outputStream.toByteArray();
        } catch (JRException | IOException e) {
            log.error("Error generating PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    /**
     * Generate PDF from a compiled JasperReport (.jasper file) and data
     * @param jasperPath Path to the compiled JasperReport in the classpath
     * @param data List of data objects to be used in the report
     * @param outputPath Path where the PDF should be saved
     * @return Path to the generated PDF file
     */
    public Path generatePdfFromJasper(String jasperPath, List<?> data, Path outputPath) {
        try {
            // Load the compiled JasperReport
            InputStream jasperStream = new ClassPathResource(jasperPath).getInputStream();
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

            // Create data source
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

            // Set parameters
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("REPORT_TITLE", "Generated Report");

            // Fill the report
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Export to PDF
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath.toString());

            return outputPath;
        } catch (JRException | IOException e) {
            log.error("Error generating PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
} 