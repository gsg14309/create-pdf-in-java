package com.example.demo.service;

import com.example.demo.config.TestFreemarkerConfig;
import com.example.demo.dto.ReportData;
import com.example.demo.dto.ReportItem;
import com.example.demo.entity.Report;
import com.example.demo.entity.ReportStatus;
import com.example.demo.exception.PDFGenerationException;
import com.example.demo.repository.ReportRepository;
import com.example.demo.util.FileStorageUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = TestFreemarkerConfig.class)
class PdfGeneratorServiceTest {

    @Mock
    private Configuration freemarkerConfig;

    @Mock
    private FileStorageUtil fileStorageUtil;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private Template template;

    @TempDir
    Path tempDir;

    @InjectMocks
    private PdfGeneratorService pdfGeneratorService;

    private ReportData reportData;
    private static final String TEMPLATE_NAME = "report.ftl";
    private Random random;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        random = new Random();
        
        // Setup the service
        pdfGeneratorService = new PdfGeneratorService(
            freemarkerConfig,
            fileStorageUtil,
            reportRepository
        );

        // Mock the template
        when(freemarkerConfig.getTemplate(anyString())).thenReturn(template);

        // Mock the report repository to return a report when saved
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
            Report report = invocation.getArgument(0);
            if (report.getReportId() == null) {
                report.setReportId(UUID.randomUUID().toString());
            }
            return report;
        });

        // Mock the file storage utility to return a path
        when(fileStorageUtil.savePdf(any(byte[].class), anyString()))
            .thenAnswer(invocation -> {
                String fileName = invocation.getArgument(1);
                return tempDir.resolve(fileName).toString();
            });
    }

    @Test
    void generatePdfForBasicReport_Success() throws Exception {
        // Arrange
        StringWriter stringWriter = new StringWriter();
        stringWriter.write("<html><body><h1>" + reportData.getTitle() + "</h1></body></html>");
        doAnswer(invocation -> {
            StringWriter writer = invocation.getArgument(1);
            writer.write(stringWriter.toString());
            return null;
        }).when(template).process(any(), any(StringWriter.class));
        
        when(fileStorageUtil.savePdf(any(), anyString())).thenReturn("/path/to/saved/file.pdf");

        // Act
        String result = pdfGeneratorService.generatePdf(reportData);

        // Assert
        assertNotNull(result);
        assertTrue(result.endsWith(".pdf"));
        verify(reportRepository).save(any(Report.class));
        verify(fileStorageUtil).savePdf(any(), anyString());
        verify(freemarkerConfig).getTemplate(eq(TEMPLATE_NAME));
    }

    @Test
    void generatePdfForBasicReport_TemplateException() throws Exception {
        // Arrange
        doThrow(new TemplateException("Template error", null)).when(template).process(any(), any(StringWriter.class));

        // Act & Assert
        assertThrows(PDFGenerationException.class, () -> 
            pdfGeneratorService.generatePdf(reportData)
        );
        verify(freemarkerConfig).getTemplate(eq(TEMPLATE_NAME));
    }

    @Test
    void generatePdfForBasicReport_FileStorageException() throws Exception {
        // Arrange
        StringWriter stringWriter = new StringWriter();
        stringWriter.write("<html><body><h1>" + reportData.getTitle() + "</h1></body></html>");
        doAnswer(invocation -> {
            StringWriter writer = invocation.getArgument(1);
            writer.write(stringWriter.toString());
            return null;
        }).when(template).process(any(), any(StringWriter.class));
        
        when(fileStorageUtil.savePdf(any(), anyString())).thenThrow(new IOException("Storage error"));

        // Act & Assert
        assertThrows(PDFGenerationException.class, () -> 
            pdfGeneratorService.generatePdf(reportData)
        );
        verify(freemarkerConfig).getTemplate(eq(TEMPLATE_NAME));
    }

    @Test
    void generatePdfForBasicReport_VerifyReportCreation() throws Exception {
        // Arrange
        StringWriter stringWriter = new StringWriter();
        stringWriter.write("<html><body><h1>" + reportData.getTitle() + "</h1></body></html>");
        doAnswer(invocation -> {
            StringWriter writer = invocation.getArgument(1);
            writer.write(stringWriter.toString());
            return null;
        }).when(template).process(any(), any(StringWriter.class));
        
        when(fileStorageUtil.savePdf(any(), anyString())).thenReturn("/path/to/saved/file.pdf");

        // Act
        pdfGeneratorService.generatePdf(reportData);

        // Assert
        verify(reportRepository).save(argThat(report -> {
            assertEquals(reportData.getTitle(), report.getTitle());
            assertNotNull(report.getReportId());
            assertEquals(ReportStatus.COMPLETED, report.getStatus());
            return true;
        }));
        verify(freemarkerConfig).getTemplate(eq(TEMPLATE_NAME));
    }

    @Test
    void testGenerateLargePdf() throws Exception {
        // Create a large report data with 500 pages worth of content
        ReportData reportData = createLargeReportData(500);
        
        // Mock template processing
        doAnswer(invocation -> {
            StringWriter writer = invocation.getArgument(1);
            writer.write(generateHtmlContent(reportData));
            return null;
        }).when(template).process(any(), any(StringWriter.class));
        
        // Generate the PDF
        String outputPath = pdfGeneratorService.generatePdf(reportData);
        
        // Verify the output
        assertNotNull(outputPath);
        File outputFile = new File(outputPath);
        assertTrue(outputFile.exists());
        assertTrue(outputFile.length() > 0);
        
        // Verify repository interactions
        verify(reportRepository, times(1)).save(any(Report.class));
        verify(reportRepository, times(1)).save(argThat(report -> 
            report.getStatus() == ReportStatus.COMPLETED
        ));
        
        // Verify template processing
        verify(template, times(1)).process(any(), any(StringWriter.class));
    }

    @Test
    void testGeneratePdfWithDifferentPageSizes() throws Exception {
        int[] pageSizes = {10, 50, 100, 500};
        
        for (int pages : pageSizes) {
            // Create report data with specified number of pages
            ReportData reportData = createLargeReportData(pages);
            
            // Mock template processing
            doAnswer(invocation -> {
                StringWriter writer = invocation.getArgument(1);
                writer.write(generateHtmlContent(reportData));
                return null;
            }).when(template).process(any(), any(StringWriter.class));
            
            // Generate the PDF
            String outputPath = pdfGeneratorService.generatePdf(reportData);
            
            // Verify the output
            assertNotNull(outputPath);
            File outputFile = new File(outputPath);
            assertTrue(outputFile.exists());
            assertTrue(outputFile.length() > 0);
            
            // Verify repository interactions
            verify(reportRepository, times(1)).save(any(Report.class));
            verify(reportRepository, times(1)).save(argThat(report -> 
                report.getStatus() == ReportStatus.COMPLETED
            ));
            
            // Reset mocks for next iteration
            reset(reportRepository);
            when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
                Report report = invocation.getArgument(0);
                if (report.getReportId() == null) {
                    report.setReportId(UUID.randomUUID().toString());
                }
                return report;
            });
        }
    }

    private ReportData createLargeReportData(int numberOfPages) {
        ReportData reportData = new ReportData();
        reportData.setReportId(UUID.randomUUID().toString());
        reportData.setTitle("Large Test Report - " + numberOfPages + " Pages");
        
        List<ReportItem> items = new ArrayList<>();
        
        // Create items for each page
        for (int page = 0; page < numberOfPages; page++) {
            // Add 5 items per page
            for (int i = 0; i < 5; i++) {
                ReportItem item = new ReportItem();
                item.setName("Item " + (page * 5 + i + 1));
                item.setDescription("Description for item " + (page * 5 + i + 1));
                item.setValue("Value " + random.nextInt(1000));
                items.add(item);
            }
        }
        
        reportData.setItems(items);
        return reportData;
    }

    private String generateHtmlContent(ReportData reportData) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><body>");
        html.append("<h1>").append(reportData.getTitle()).append("</h1>");
        html.append("<table border='1'>");
        html.append("<tr><th>Name</th><th>Description</th><th>Value</th></tr>");
        
        for (ReportItem item : reportData.getItems()) {
            html.append("<tr>");
            html.append("<td>").append(item.getName()).append("</td>");
            html.append("<td>").append(item.getDescription()).append("</td>");
            html.append("<td>").append(item.getValue()).append("</td>");
            html.append("</tr>");
        }
        
        html.append("</table></body></html>");
        return html.toString();
    }

    private String generateRandomText(int length) {
        StringBuilder sb = new StringBuilder();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }
} 