package com.example.demo;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public class FreeMarkerTest {
    @Data
    public static class ReportItem {
        private String name;
        private double value;
        private String status;
        private boolean highlight;
        private Map<String, Object> attributes;
    }

    @Data
    public static class ReportStatistics {
        private double totalValue;
        private double averageValue;
        private int itemCount;
        private Map<String, Double> categoryTotals;
    }

    @Data
    public static class ReportMetadata {
        private String version;
        private String environment;
        private LocalDateTime generatedAt;
        private Map<String, String> additionalInfo;
    }

    @Data
    public static class ChartData {
        private String name;
        private double value;
    }

    @Data
    public static class Report {
        private String title;
        private String subtitle;
        private String status;
        private List<ReportItem> items;
        private ReportStatistics statistics;
        private ReportMetadata metadata;
        private List<ChartData> chartData;
        private Map<String, Object> summary;
    }

    public static void main(String[] args) {
        try {
            // Create FreeMarker configuration
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
            cfg.setClassLoaderForTemplateLoading(FreeMarkerTest.class.getClassLoader(), "templates");
            cfg.setDefaultEncoding("UTF-8");

            // Load template
            Template template = cfg.getTemplate("advanced-report.ftl");

            // Create complex data model
            Report report = createSampleReport();

            // Process template
            StringWriter writer = new StringWriter();
            template.process(report, writer);

            // Output result
            System.out.println(writer.toString());
        } catch (IOException | TemplateException e) {
            log.error("Error processing template", e);
        }
    }

    private static Report createSampleReport() {
        Report report = new Report();
        report.setTitle("Advanced Sales Report");
        report.setSubtitle("Q1 2024 Performance Analysis");
        report.setStatus("COMPLETED");

        // Create items
        List<ReportItem> items = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= 5; i++) {
            ReportItem item = new ReportItem();
            item.setName("Product " + i);
            item.setValue(random.nextDouble() * 1000);
            item.setStatus(i % 3 == 0 ? "PROCESSING" : "COMPLETED");
            item.setHighlight(i % 2 == 0);

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("category", "Category " + (i % 3 + 1));
            attributes.put("stock", random.nextInt(100));
            item.setAttributes(attributes);

            items.add(item);
        }
        report.setItems(items);

        // Create statistics
        ReportStatistics stats = new ReportStatistics();
        stats.setItemCount(items.size());
        stats.setTotalValue(items.stream().mapToDouble(ReportItem::getValue).sum());
        stats.setAverageValue(stats.getTotalValue() / items.size());

        Map<String, Double> categoryTotals = new HashMap<>();
        items.forEach(item -> {
            String category = (String) item.getAttributes().get("category");
            categoryTotals.merge(category, item.getValue(), Double::sum);
        });
        stats.setCategoryTotals(categoryTotals);
        report.setStatistics(stats);

        // Create metadata
        ReportMetadata metadata = new ReportMetadata();
        metadata.setVersion("1.0.0");
        metadata.setEnvironment("Production");
        metadata.setGeneratedAt(LocalDateTime.now());

        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("generatedBy", "System");
        additionalInfo.put("reportType", "Sales");
        metadata.setAdditionalInfo(additionalInfo);
        report.setMetadata(metadata);

        // Create chart data
        List<ChartData> chartData = new ArrayList<>();
        categoryTotals.forEach((category, value) -> {
            ChartData data = new ChartData();
            data.setName(category);
            data.setValue(value);
            chartData.add(data);
        });
        report.setChartData(chartData);

        // Create summary
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalProducts", items.size());
        summary.put("totalValue", stats.getTotalValue());
        summary.put("averageValue", stats.getAverageValue());
        summary.put("categories", categoryTotals.keySet().size());
        report.setSummary(summary);

        return report;
    }
} 