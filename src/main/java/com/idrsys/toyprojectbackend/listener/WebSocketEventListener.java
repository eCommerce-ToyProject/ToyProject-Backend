package com.idrsys.toyprojectbackend.listener;

import com.idrsys.toyprojectbackend.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * WebSocket 연결 성공 이벤트
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("새로운 WebSocket 연결: {}", event.getMessage());
    }

    /**
     * WebSocket 연결 해제 이벤트
     * 사용자가 브라우저를 닫거나 연결이 끊어질 때 자동으로 퇴장 처리
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        
        // 세션에서 사용자 정보 조회
        Integer memNo = (Integer) headerAccessor.getSessionAttributes().get("memNo");
        Long liveNo = (Long) headerAccessor.getSessionAttributes().get("liveNo");
        
        if (memNo != null && liveNo != null) {
            log.info("사용자 연결 해제: memNo={}, liveNo={}", memNo, liveNo);
            
            try {
                // 자동 퇴장 처리
                chatService.leaveLiveRoom(liveNo, memNo);
                
                // 현재 접속자 수 업데이트
                long userCount = chatService.getCurrentUserCount(liveNo);
                messagingTemplate.convertAndSend("/topic/live/" + liveNo + "/usercount", userCount);
                
            } catch (Exception e) {
                log.error("사용자 퇴장 처리 중 오류: memNo={}, liveNo={}", memNo, liveNo, e);
            }
        }
    }
}
