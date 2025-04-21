package com.aimarketing.controllers.api;

import com.aimarketing.models.Campaign;
import com.aimarketing.services.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/marketing")
public class MarketingAPIController {
    private final AIService aiService;
    private final SEOService seoService;
    private final ContentWritingService contentWritingService;
    private final SocialMediaService socialMediaService;
    private final VideoEditingService videoEditingService;
    
    public MarketingAPIController() {
        this.aiService = new AIService();
        this.seoService = new SEOService();
        this.contentWritingService = new ContentWritingService();
        this.socialMediaService = new SocialMediaService();
        this.videoEditingService = new VideoEditingService();
    }
    
    @PostMapping("/optimize")
    public ResponseEntity<Map<String, Object>> optimizeCampaign(
            @RequestBody Campaign campaign,
            @RequestParam String targetMetric,
            @RequestParam(defaultValue = "0.1") double learningRate) {
        try {
            aiService.optimizeCampaign(campaign, targetMetric, learningRate);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Campaign optimization started");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/seo/analyze")
    public ResponseEntity<Map<String, Object>> analyzeSEO(
            @RequestBody Map<String, String> content) {
        try {
            Map<String, Object> analysis = seoService.analyzeContent(content.get("content"));
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/content/generate")
    public ResponseEntity<Map<String, Object>> generateContent(
            @RequestBody Map<String, Object> request) {
        try {
            String topic = (String) request.get("topic");
            String type = (String) request.get("type");
            int wordCount = (Integer) request.get("wordCount");
            
            String content = contentWritingService.generateContent(topic, type, wordCount);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("content", content);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/social/analyze")
    public ResponseEntity<Map<String, Object>> analyzeSocialMedia(
            @RequestBody Campaign campaign) {
        try {
            Map<String, Object> analysis = socialMediaService.analyzeCampaign(campaign);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/video/edit")
    public ResponseEntity<Map<String, Object>> editVideo(
            @RequestBody Map<String, Object> request) {
        try {
            String videoUrl = (String) request.get("videoUrl");
            List<String> effects = (List<String>) request.get("effects");
            
            String editedVideoUrl = videoEditingService.editVideo(videoUrl, effects);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("editedVideoUrl", editedVideoUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/metrics/predict")
    public ResponseEntity<Map<String, Double>> predictMetrics(
            @RequestBody Campaign campaign,
            @RequestParam List<String> metrics) {
        try {
            Map<String, Double> predictions = aiService.predictMetrics(campaign, metrics);
            return ResponseEntity.ok(predictions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}