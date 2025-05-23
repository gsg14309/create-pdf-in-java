package com.example.demo.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.Map;

@Service
public class TemplateService {

    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);

   // @Autowired
  //  @Qualifier("customFreemarkerConfig")
    private Configuration freemarkerConfig;

    public String processTemplate(String templateName, Map<String, Object> data) throws IOException, TemplateException {
        logger.info("Processing template: {}", templateName);
        logger.debug("Template data: {}", data);
        
        try {
            Template template = freemarkerConfig.getTemplate(templateName + ".ftl");
            logger.debug("Template loaded successfully");
            
            String result = FreeMarkerTemplateUtils.processTemplateIntoString(template, data);
            logger.debug("Template processed successfully");
            logger.info("Template processing completed for: {}", templateName);
            
            return result;
        } catch (IOException e) {
            logger.error("Error loading template {}: {}", templateName, e.getMessage(), e);
            throw new IOException("Error loading template: " + templateName, e);
        } catch (TemplateException e) {
            logger.error("Error processing template {}: {}", templateName, e.getMessage(), e);
            throw new TemplateException("Error processing template: " + templateName, e, null);
        }
    }
} 