package org.example.gsu.pdfreportdocs;

import com.example.demo.dto.ReportData;
import com.example.demo.dto.ReportItem;

import java.util.ArrayList;
import java.util.List;

public class SampleReportData {

    public  ReportData createSampleReportData() {
        ReportData reportData = new ReportData();
        reportData.setTitle("Sample Report");

        List<ReportItem> items = new ArrayList<>();

        ReportItem item1 = new ReportItem();
        item1.setName("Item 1");
        item1.setDescription("Description for item 1");
        item1.setValue("100");
        items.add(item1);

        ReportItem item2 = new ReportItem();
        item2.setName("Item 2");
        item2.setDescription("Description for item 2");
        item2.setValue("200");
        items.add(item2);

        reportData.setItems(items);
        return reportData;
    }
}
