package com.example.demo.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ReportData {
    private String reportId;
    private String title;
    private String createdBy;
    private List<ReportItem> items;
    private Map<String, Object> data;
} 