package com.idrsys.toyprojectbackend.controller.webrtc;

import com.idrsys.toyprojectbackend.service.WebRTCSignalingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/webrtc")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WebRTCController {
    
    private final WebRTCSignalingService signalingService;
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("roomCount", signalingService.getRoomCount());
        stats.put("totalUsers", signalingService.getTotalUsers());
        stats.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "WebRTC Signaling Server");
        return ResponseEntity.ok(response);
    }
}
