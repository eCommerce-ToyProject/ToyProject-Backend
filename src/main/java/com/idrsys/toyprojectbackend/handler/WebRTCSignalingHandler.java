package com.idrsys.toyprojectbackend.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idrsys.toyprojectbackend.dto.webrtc.SignalingMessage;
import com.idrsys.toyprojectbackend.service.WebRTCSignalingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebRTCSignalingHandler implements WebSocketHandler {
    
    private final WebRTCSignalingService signalingService;
    private final ObjectMapper objectMapper;
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebRTC WebSocket connection established: {}", session.getId());
    }
    
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        try {
            String payload = (String) message.getPayload();
            log.debug("Received message from {}: {}", session.getId(), payload);
            
            SignalingMessage signalingMessage = objectMapper.readValue(payload, SignalingMessage.class);
            signalingService.handleMessage(session, signalingMessage);
            
        } catch (Exception e) {
            log.error("Error handling WebSocket message from session {}", session.getId(), e);
            session.sendMessage(new TextMessage(
                "{\"type\":\"error\",\"data\":{\"message\":\"Invalid message format\"}}"
            ));
        }
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket transport error for session {}", session.getId(), exception);
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("WebRTC WebSocket connection closed: {} with status: {}", session.getId(), closeStatus);
        signalingService.handleSessionClosed(session);
    }
    
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
