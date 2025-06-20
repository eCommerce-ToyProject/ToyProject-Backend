package com.idrsys.toyprojectbackend.controller.chat;

import com.idrsys.toyprojectbackend.dto.chat.LiveRoomCreateDto;
import com.idrsys.toyprojectbackend.dto.chat.LiveRoomDto;
import com.idrsys.toyprojectbackend.dto.chat.LiveRoomUpdateDto;
import com.idrsys.toyprojectbackend.service.LiveRoomService;
import com.idrsys.toyprojectbackend.service.WebRTCSignalingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/live")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Live Room API", description = "라이브 방송 관리 API")
public class LiveRoomController {

    private final LiveRoomService liveRoomService;
    private final WebRTCSignalingService webRTCSignalingService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 라이브 방송 생성
     */
    @PostMapping
    @Operation(summary = "라이브 방송 생성", description = "새로운 라이브 방송을 생성합니다.")
    public ResponseEntity<LiveRoomDto> createLiveRoom(
            @RequestBody @Validated LiveRoomCreateDto createDto) {
        
        LiveRoomDto liveRoom = liveRoomService.createLiveRoom(createDto);
        log.info("Live room created: liveNo={}, title={}", liveRoom.getLiveNo(), liveRoom.getTitle());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(liveRoom);
    }

    /**
     * 라이브 방송 시작
     */
    @PostMapping("/{liveNo}/start")
    @Operation(summary = "라이브 방송 시작", description = "예정된 라이브 방송을 시작합니다.")
    public ResponseEntity<Map<String, Object>> startLiveRoom(
            @Parameter(description = "라이브 방송 번호") @PathVariable Long liveNo,
            @Parameter(description = "호스트 회원 ID") @RequestParam String hostMemberId) {
        
        LiveRoomDto liveRoom = liveRoomService.startLiveRoom(liveNo, hostMemberId);
        log.info("Live room started: liveNo={}, host={}", liveNo, hostMemberId);
        
        // 채팅방에 방송 시작 알림
        try {
            Map<String, Object> startNotification = new HashMap<>();
            startNotification.put("type", "BROADCAST_START");
            startNotification.put("message", "🎥 " + liveRoom.getTitle() + " 방송이 시작되었습니다!");
            startNotification.put("liveNo", liveNo);
            startNotification.put("hostName", liveRoom.getHostMemName());
            
            messagingTemplate.convertAndSend("/topic/live/" + liveNo, startNotification);
            log.info("Broadcast start notification sent to chat room: {}", liveNo);
        } catch (Exception e) {
            log.warn("Failed to send broadcast start notification: {}", e.getMessage());
        }
        
        // 응답에 WebRTC 연결 정보 포함
        Map<String, Object> response = new HashMap<>();
        response.put("liveRoom", liveRoom);
        response.put("webrtcRoom", liveNo.toString()); // WebRTC 방 ID
        response.put("chatRoom", liveNo); // 채팅 방 번호
        response.put("signalingUrl", "ws://localhost:8080/signaling"); // WebSocket URL
        response.put("message", "방송이 시작되었습니다. WebRTC 연결을 시작하세요.");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 라이브 방송 종료
     */
    @PostMapping("/{liveNo}/end")
    @Operation(summary = "라이브 방송 종료", description = "진행 중인 라이브 방송을 종료합니다.")
    public ResponseEntity<LiveRoomDto> endLiveRoom(
            @Parameter(description = "라이브 방송 번호") @PathVariable Long liveNo,
            @Parameter(description = "호스트 회원 ID") @RequestParam String hostMemberId) {
        
        LiveRoomDto liveRoom = liveRoomService.endLiveRoom(liveNo, hostMemberId);
        log.info("Live room ended: liveNo={}, host={}", liveNo, hostMemberId);
        
        // 채팅방에 방송 종료 알림
        try {
            Map<String, Object> endNotification = new HashMap<>();
            endNotification.put("type", "BROADCAST_END");
            endNotification.put("message", "📺 방송이 종료되었습니다. 시청해주셔서 감사합니다!");
            endNotification.put("liveNo", liveNo);
            
            messagingTemplate.convertAndSend("/topic/live/" + liveNo, endNotification);
            log.info("Broadcast end notification sent to chat room: {}", liveNo);
        } catch (Exception e) {
            log.warn("Failed to send broadcast end notification: {}", e.getMessage());
        }
        
        return ResponseEntity.ok(liveRoom);
    }

    /**
     * 라이브 방송 정보 수정
     */
    @PutMapping("/{liveNo}")
    @Operation(summary = "라이브 방송 정보 수정", description = "라이브 방송 정보를 수정합니다.")
    public ResponseEntity<LiveRoomDto> updateLiveRoom(
            @Parameter(description = "라이브 방송 번호") @PathVariable Long liveNo,
            @Parameter(description = "호스트 회원 ID") @RequestParam String hostMemberId,
            @RequestBody LiveRoomUpdateDto updateDto) {
        
        LiveRoomDto liveRoom = liveRoomService.updateLiveRoom(liveNo, hostMemberId, updateDto);
        return ResponseEntity.ok(liveRoom);
    }

    /**
     * 라이브 방송 상세 조회
     */
    @GetMapping("/{liveNo}")
    @Operation(summary = "라이브 방송 상세 조회", description = "특정 라이브 방송의 상세 정보를 조회합니다.")
    public ResponseEntity<LiveRoomDto> getLiveRoom(
            @Parameter(description = "라이브 방송 번호") @PathVariable Long liveNo) {
        
        LiveRoomDto liveRoom = liveRoomService.getLiveRoom(liveNo);
        return ResponseEntity.ok(liveRoom);
    }

    /**
     * 현재 라이브 중인 방송 목록 조회
     */
    @GetMapping
    @Operation(summary = "라이브 방송 목록 조회", description = "현재 진행 중인 라이브 방송 목록을 조회합니다.")
    public ResponseEntity<Page<LiveRoomDto>> getLiveRooms(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Page<LiveRoomDto> liveRooms = liveRoomService.getLiveRooms(page, size);
        return ResponseEntity.ok(liveRooms);
    }

    /**
     * 특정 호스트의 라이브 방송 목록 조회
     */
    @GetMapping("/host/{hostMemberId}")
    @Operation(summary = "호스트별 라이브 방송 조회", description = "특정 호스트의 라이브 방송 목록을 조회합니다.")
    public ResponseEntity<List<LiveRoomDto>> getLiveRoomsByHost(
            @Parameter(description = "호스트 회원 ID") @PathVariable String hostMemberId) {
        
        List<LiveRoomDto> liveRooms = liveRoomService.getLiveRoomsByHost(hostMemberId);
        return ResponseEntity.ok(liveRooms);
    }

    /**
     * 카테고리별 라이브 방송 목록 조회
     */
    @GetMapping("/category/{catCd}")
    @Operation(summary = "카테고리별 라이브 방송 조회", description = "특정 카테고리의 라이브 방송 목록을 조회합니다.")
    public ResponseEntity<List<LiveRoomDto>> getLiveRoomsByCategory(
            @Parameter(description = "카테고리 코드") @PathVariable Integer catCd) {
        
        List<LiveRoomDto> liveRooms = liveRoomService.getLiveRoomsByCategory(catCd);
        return ResponseEntity.ok(liveRooms);
    }

    /**
     * 라이브 방송 삭제
     */
    @DeleteMapping("/{liveNo}")
    @Operation(summary = "라이브 방송 삭제", description = "라이브 방송을 삭제합니다. (진행 중이 아닌 경우만 가능)")
    public ResponseEntity<Void> deleteLiveRoom(
            @Parameter(description = "라이브 방송 번호") @PathVariable Long liveNo,
            @Parameter(description = "호스트 회원 ID") @RequestParam String hostMemberId) {
        
        liveRoomService.deleteLiveRoom(liveNo, hostMemberId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 라이브 방송 접속자 수 조회
     */
    @GetMapping("/{liveNo}/usercount")
    @Operation(summary = "라이브 방송 접속자 수 조회", description = "현재 라이브 방송의 접속자 수를 조회합니다.")
    public ResponseEntity<Map<String, Object>> getCurrentUserCount(
            @Parameter(description = "라이브 방송 번호") @PathVariable Long liveNo) {
        
        Long chatUserCount = liveRoomService.getCurrentUserCount(liveNo);
        
        // WebRTC 접속자 수도 포함
        int webrtcUserCount = webRTCSignalingService.getRoomUserCount(liveNo.toString());
        
        Map<String, Object> userCounts = new HashMap<>();
        userCounts.put("chatUsers", chatUserCount);
        userCounts.put("webrtcUsers", webrtcUserCount);
        userCounts.put("totalUsers", Math.max(chatUserCount, webrtcUserCount)); // 더 큰 값 사용
        
        return ResponseEntity.ok(userCounts);
    }

    /**
     * 라이브 방송 WebRTC 연결 정보 조회
     */
    @GetMapping("/{liveNo}/webrtc-info")
    @Operation(summary = "WebRTC 연결 정보", description = "라이브 방송의 WebRTC 연결 정보를 조회합니다.")
    public ResponseEntity<Map<String, Object>> getWebRTCInfo(
            @Parameter(description = "라이브 방송 번호") @PathVariable Long liveNo) {
        
        LiveRoomDto liveRoom = liveRoomService.getLiveRoom(liveNo);
        
        Map<String, Object> webrtcInfo = new HashMap<>();
        webrtcInfo.put("liveNo", liveNo);
        webrtcInfo.put("roomId", liveNo.toString()); // WebRTC 방 ID
        webrtcInfo.put("signalingUrl", "ws://localhost:8080/signaling");
        webrtcInfo.put("chatUrl", "ws://localhost:8080/ws");
        webrtcInfo.put("status", liveRoom.getStatus());
        webrtcInfo.put("title", liveRoom.getTitle());
        webrtcInfo.put("hostMemberId", liveRoom.getHostMemberId());
        
        return ResponseEntity.ok(webrtcInfo);
    }
}
