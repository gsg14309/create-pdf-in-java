package com.example.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class FileStorageUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(FileStorageUtil.class);

    private static final String PDF_STORAGE_DIR = "pdf-storage";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    public String savePdf(byte[] pdfContent, String fileName) throws IOException {
        logger.info("Saving PDF file: {}", fileName);
        
        // Create storage directory if it doesn't exist
        Path storageDir = Paths.get( PDF_STORAGE_DIR);
        if (!Files.exists(storageDir)) {
            logger.debug("Creating PDF storage directory: {}", storageDir);
            Files.createDirectories(storageDir);
        }

        // Generate unique filename with timestamp
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String uniqueFileName = String.format("%s_%s.pdf", 
            fileName.replace(".pdf", ""), timestamp);
        
        // Save the file
        Path filePath = storageDir.resolve(uniqueFileName);
        logger.debug("Saving PDF to: {}", filePath);
        Files.write(filePath, pdfContent);
        
        logger.info("PDF saved successfully at: {}", filePath);
        return filePath.toString();
    }

    public File getPdfFile(String filePath) {
        logger.debug("Retrieving PDF file: {}", filePath);
        return new File(filePath);
    }
} 