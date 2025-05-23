package com.example.demo.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ReportData {
    private String title;
    private List<ReportItem> items;
    private Map<String, Object> data;
} 