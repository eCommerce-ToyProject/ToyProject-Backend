package com.idrsys.toyprojectbackend.controller.chat;

import com.idrsys.toyprojectbackend.service.LiveScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/live/schedule")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Live Schedule API", description = "라이브 방송 스케줄 관리 API")
public class LiveScheduleController {

    private final LiveScheduleService liveScheduleService;

    /**
     * 스케줄 상태 조회
     */
    @GetMapping("/{liveNo}/status")
    @Operation(summary = "스케줄 상태 조회", description = "특정 라이브 방송의 스케줄 상태를 조회합니다.")
    public ResponseEntity<Map<String, Object>> getScheduleStatus(
            @Parameter(description = "라이브 방송 번호") @PathVariable Long liveNo) {
        
        boolean isScheduled = liveScheduleService.isScheduled(liveNo);
        
        Map<String, Object> response = new HashMap<>();
        response.put("liveNo", liveNo);
        response.put("isScheduled", isScheduled);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 전체 스케줄 통계 조회
     */
    @GetMapping("/stats")
    @Operation(summary = "스케줄 통계 조회", description = "전체 활성 스케줄 통계를 조회합니다.")
    public ResponseEntity<Map<String, Object>> getScheduleStats() {
        
        int activeScheduleCount = liveScheduleService.getActiveScheduleCount();
        
        Map<String, Object> response = new HashMap<>();
        response.put("activeScheduleCount", activeScheduleCount);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 스케줄 취소 (관리자용)
     */
    @DeleteMapping("/{liveNo}")
    @Operation(summary = "스케줄 취소", description = "특정 라이브 방송의 스케줄을 취소합니다.")
    public ResponseEntity<Map<String, String>> cancelSchedule(
            @Parameter(description = "라이브 방송 번호") @PathVariable Long liveNo) {
        
        liveScheduleService.cancelScheduledTask(liveNo);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "스케줄이 취소되었습니다.");
        response.put("liveNo", liveNo.toString());
        
        return ResponseEntity.ok(response);
    }
}
