package com.example.demo.service;

import com.example.demo.config.TestFreemarkerConfig;
import com.example.demo.dto.ReportData;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Optional;

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

    @InjectMocks
    private PdfGeneratorService pdfGeneratorService;

    private ReportData reportData;
    private static final String TEMPLATE_NAME = "report.ftl";

    @BeforeEach
    void setUp() throws IOException {
        reportData = new ReportData();
        reportData.setTitle("Test Report");
        // Add other necessary test data

        // Mock template loading
        when(freemarkerConfig.getTemplate(eq(TEMPLATE_NAME))).thenReturn(template);
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
        String result = pdfGeneratorService.generatePdfForBasicReport(reportData);

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
            pdfGeneratorService.generatePdfForBasicReport(reportData)
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
            pdfGeneratorService.generatePdfForBasicReport(reportData)
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
        pdfGeneratorService.generatePdfForBasicReport(reportData);

        // Assert
        verify(reportRepository).save(argThat(report -> {
            assertEquals(reportData.getTitle(), report.getTitle());
            assertNotNull(report.getReportId());
            assertEquals(ReportStatus.COMPLETED, report.getStatus());
            return true;
        }));
        verify(freemarkerConfig).getTemplate(eq(TEMPLATE_NAME));
    }
} 