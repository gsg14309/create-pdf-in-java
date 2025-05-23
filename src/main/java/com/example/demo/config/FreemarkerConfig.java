package com.example.demo.config;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;


@org.springframework.context.annotation.Configuration
public class FreemarkerConfig {

    private static final Logger logger = LoggerFactory.getLogger(FreemarkerConfig.class);

    @Bean(name = "customFreemarkerConfig")
     public Configuration customFreemarkerConfig() {
        logger.info("Initializing custom FreeMarker configuration");
        
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        configuration.setClassLoaderForTemplateLoading(
            this.getClass().getClassLoader(), "templates");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);
        configuration.setFallbackOnNullLoopVariable(false);
        
        // Set XML settings
        configuration.setTagSyntax(Configuration.AUTO_DETECT_TAG_SYNTAX);
        configuration.setInterpolationSyntax(Configuration.DOLLAR_INTERPOLATION_SYNTAX);
        
        logger.debug("FreeMarker configuration completed with settings: version={}, encoding={}, tagSyntax={}, interpolationSyntax={}",
            Configuration.VERSION_2_3_32,
            "UTF-8",
            Configuration.AUTO_DETECT_TAG_SYNTAX,
            Configuration.DOLLAR_INTERPOLATION_SYNTAX);
        
        return configuration;
    }


} 