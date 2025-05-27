package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ReportData {
    @NotBlank(message = "Report ID is required")
    private String reportId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Items list cannot be null")
    private List<ReportItem> items;

    private Map<String, Object> data;
}