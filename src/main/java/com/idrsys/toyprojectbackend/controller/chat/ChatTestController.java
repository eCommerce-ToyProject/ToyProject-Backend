package com.idrsys.toyprojectbackend.controller.chat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat/test")
@Tag(name = "Chat Test API", description = "채팅 시스템 상태 확인용 테스트 API")
public class ChatTestController {

    @GetMapping("/health")
    @Operation(summary = "채팅 시스템 상태 확인", description = "WebSocket 채팅 서버 상태를 확인합니다.")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "WebSocket Chat Server is running");
        response.put("features", new String[]{
            "Real-time chat",
            "User presence tracking", 
            "Chat history",
            "Auto disconnect handling"
        });
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/websocket-info")
    @Operation(summary = "WebSocket 연결 정보", description = "WebSocket 연결에 필요한 정보를 제공합니다.")
    public ResponseEntity<Map<String, Object>> getWebSocketInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("endpoint", "/ws");
        response.put("protocols", new String[]{"websocket", "sockjs"});
        
        Map<String, String> subscriptions = new HashMap<>();
        subscriptions.put("chat_messages", "/topic/live/{liveNo}");
        subscriptions.put("user_count", "/topic/live/{liveNo}/usercount");
        subscriptions.put("errors", "/user/queue/errors");
        response.put("subscriptions", subscriptions);
        
        Map<String, String> destinations = new HashMap<>();
        destinations.put("send_message", "/app/chat.sendMessage/{liveNo}");
        destinations.put("join_room", "/app/chat.join/{liveNo}");
        destinations.put("leave_room", "/app/chat.leave/{liveNo}");
        response.put("destinations", destinations);
        
        return ResponseEntity.ok(response);
    }
}
