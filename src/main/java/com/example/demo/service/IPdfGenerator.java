package com.example.demo.service;

import com.example.demo.dto.ReportData;

import java.nio.file.Path;
import java.util.List;

public interface IPdfGenerator {


    public String generatePdf(ReportData reportData);


}