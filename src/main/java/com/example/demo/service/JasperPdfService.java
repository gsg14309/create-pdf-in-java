package com.example.demo.service;

import com.example.demo.dto.ReportData;
import com.example.demo.dto.ReportItem;
import com.example.demo.exception.PDFGenerationException;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("jasperPdfService")
public class JasperPdfService implements IPdfGenerator {

    @Override
    public String generatePdf(ReportData reportData) {
        log.info("Starting PDF generation for report with ID: {}", reportData.getReportId());
        log.debug("Report data: title={}, items count={}", reportData.getTitle(), reportData.getItems().size());
        
        try {
            // Load the JRXML template
            String templatePath = "reports/sample-report.jrxml";
            log.debug("Loading JRXML template from: {}", templatePath);
            
            JasperReport jasperReport = JasperCompileManager.compileReport(
                    getClass().getClassLoader().getResourceAsStream(templatePath));
            log.debug("Successfully compiled JRXML template");

            // Convert ReportData items to a list of maps
            log.debug("Converting ReportData items to list of maps");
            List<Map<String, Object>> dataList = new ArrayList<>();
            for (ReportItem item : reportData.getItems()) {
                Map<String, Object> row = new HashMap<>();
                row.put("name", item.getName());
                row.put("description", item.getDescription());
                row.put("value", item.getValue());
                dataList.add(row);
                log.trace("Added item to data list: name={}, description={}, value={}", 
                    item.getName(), item.getDescription(), item.getValue());
            }
            log.debug("Converted {} items to data list", dataList.size());

            // Create the data source
            log.debug("Creating JRBeanCollectionDataSource");
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataList);

            // Set parameters
            log.debug("Setting report parameters");
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("REPORT_TITLE", reportData.getTitle());
            parameters.put("REPORT_ID", reportData.getReportId());
            parameters.put("GENERATED_DATE", new Date());
            log.trace("Report parameters: title={}, id={}, generatedDate={}", 
                reportData.getTitle(), reportData.getReportId(), new Date());

            // Create a temporary file for the PDF
            log.debug("Creating temporary file for PDF output");
            File tempFile = File.createTempFile("report", ".pdf");
            log.debug("Temporary file created at: {}", tempFile.getAbsolutePath());

            // Fill the report and export to PDF
            log.debug("Filling report with data");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            log.debug("Exporting report to PDF");
            JasperExportManager.exportReportToPdfFile(jasperPrint, tempFile.getAbsolutePath());
            log.info("PDF generation completed successfully. Output file: {}", tempFile.getAbsolutePath());

            return tempFile.getAbsolutePath();
        } catch (Exception e) {
            log.error("Error generating PDF: {}", e.getMessage(), e);
            throw new PDFGenerationException("Failed to generate PDF using JasperReports", e);
        }
    }
} 