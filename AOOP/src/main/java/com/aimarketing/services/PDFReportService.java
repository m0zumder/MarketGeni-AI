package com.aimarketing.services;

import com.aimarketing.models.Campaign;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import java.io.File;
import java.util.List;
import java.util.Map;

public class PDFReportService {
    
    public void exportToPdf(Map<String, Object> report, File file) throws Exception {
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        // Add title
        Paragraph title = new Paragraph("Campaign Performance Report")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(20);
        document.add(title);
        document.add(new Paragraph("\n"));
        
        // Add metrics table
        @SuppressWarnings("unchecked")
        Map<String, Double> metrics = (Map<String, Double>) report.get("current_metrics");
        if (metrics != null) {
            document.add(new Paragraph("Performance Metrics")
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(16));
            
            Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
            table.addCell(new Cell().add(new Paragraph("Metric").setBold()));
            table.addCell(new Cell().add(new Paragraph("Value").setBold()));
            
            for (Map.Entry<String, Double> entry : metrics.entrySet()) {
                table.addCell(new Cell().add(new Paragraph(formatMetricName(entry.getKey()))));
                table.addCell(new Cell().add(new Paragraph(formatMetricValue(entry.getKey(), entry.getValue()))));
            }
            
            document.add(table);
            document.add(new Paragraph("\n"));
        }
        
        // Add AI insights
        @SuppressWarnings("unchecked")
        List<String> insights = (List<String>) report.get("ai_insights");
        if (insights != null && !insights.isEmpty()) {
            document.add(new Paragraph("AI Insights")
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(16));
            
            for (String insight : insights) {
                document.add(new Paragraph("• " + insight));
            }
        }
        
        document.close();
    }
    
    private String formatMetricName(String key) {
        return key.substring(0, 1).toUpperCase() +
               key.substring(1).replace('_', ' ');
    }
    
    private String formatMetricValue(String key, Double value) {
        switch (key.toLowerCase()) {
            case "ctr":
            case "conversion_rate":
                return String.format("%.2f%%", value * 100);
            case "cpa":
            case "ad_spend":
                return String.format("$%.2f", value);
            case "roas":
                return String.format("%.2fx", value);
            default:
                return String.format("%.0f", value);
        }
    }
}