package com.aimarketing.controllers.api;

import com.aimarketing.apis.http.APIResponseHandler;
import com.aimarketing.apis.middleware.APIValidationMiddleware;
import com.aimarketing.services.VideoEditingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/video")
public class VideoAPIController {
    private final VideoEditingService videoEditingService;
    
    public VideoAPIController() {
        this.videoEditingService = new VideoEditingService();
    }
    
    @PostMapping("/edit")
    public ResponseEntity<Map<String, Object>> editVideo(
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String token) {
        try {
            APIValidationMiddleware.validateRequest("/api/video/edit", "POST", token, "video");
            
            String videoUrl = (String) request.get("videoUrl");
            Map<String, Object> editingParams = (Map<String, Object>) request.get("params");
            
            Map<String, Object> result = videoEditingService.editVideo(videoUrl, editingParams);
            return APIResponseHandler.success(result, "Video editing started successfully");
        } catch (Exception e) {
            return APIResponseHandler.error(e.getMessage(), e.getClass().getSimpleName(), org.springframework.http.HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeVideo(
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String token) {
        try {
            APIValidationMiddleware.validateRequest("/api/video/analyze", "POST", token, "video");
            
            String videoUrl = (String) request.get("videoUrl");
            String analysisType = (String) request.get("analysisType");
            
            Map<String, Object> analysis = videoEditingService.analyzeVideo(videoUrl, analysisType);
            return APIResponseHandler.success(analysis);
        } catch (Exception e) {
            return APIResponseHandler.error(e.getMessage(), e.getClass().getSimpleName(), org.springframework.http.HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/status/{jobId}")
    public ResponseEntity<Map<String, Object>> getVideoJobStatus(
            @PathVariable String jobId,
            @RequestHeader("Authorization") String token) {
        try {
            APIValidationMiddleware.validateRequest("/api/video/status/" + jobId, "GET", token, "video");
            
            Map<String, Object> status = videoEditingService.getJobStatus(jobId);
            return APIResponseHandler.success(status);
        } catch (Exception e) {
            return APIResponseHandler.error(e.getMessage(), e.getClass().getSimpleName(), org.springframework.http.HttpStatus.BAD_REQUEST);
        }
    }
}