package com.idrsys.toyprojectbackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idrsys.toyprojectbackend.dto.webrtc.SignalingMessage;
import com.idrsys.toyprojectbackend.dto.webrtc.WebRTCRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebRTCSignalingService {
    
    private final ObjectMapper objectMapper;
    private final Map<String, WebRTCRoom> rooms = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToUserId = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToRoomId = new ConcurrentHashMap<>();
    
    public void handleMessage(WebSocketSession session, SignalingMessage message) {
        try {
            String sessionId = session.getId();
            
            switch (message.getType()) {
                case "join-room":
                    handleJoinRoom(session, message);
                    break;
                case "offer":
                    handleOffer(session, message);
                    break;
                case "answer":
                    handleAnswer(session, message);
                    break;
                case "ice-candidate":
                    handleIceCandidate(session, message);
                    break;
                default:
                    log.warn("Unknown message type: {}", message.getType());
            }
        } catch (Exception e) {
            log.error("Error handling signaling message", e);
            sendErrorMessage(session, "Internal server error");
        }
    }
    
    private void handleJoinRoom(WebSocketSession session, SignalingMessage message) {
        String roomId = message.getRoomId();
        String userId = message.getUserId();
        Boolean isBroadcaster = message.getIsBroadcaster();
        
        // data 객체에서도 isBroadcaster 확인 (클라이언트 호환성)
        if (isBroadcaster == null && message.getData() != null) {
            Object broadcasterObj = message.getData().get("isBroadcaster");
            if (broadcasterObj instanceof Boolean) {
                isBroadcaster = (Boolean) broadcasterObj;
            }
        }
        
        log.info("Join room request - roomId: {}, userId: {}, isBroadcaster: {}", roomId, userId, isBroadcaster);
        
        if (roomId == null || userId == null || isBroadcaster == null) {
            log.warn("Invalid join-room message - missing required fields");
            sendErrorMessage(session, "Invalid join-room message: missing roomId, userId, or isBroadcaster");
            return;
        }
        
        // roomId가 숫자인 경우 liveNo로 간주 (예: "123")
        // roomId가 "room123" 형태인 경우 숫자 부분 추출
        Long liveNo = null;
        try {
            if (roomId.startsWith("room")) {
                liveNo = Long.parseLong(roomId.substring(4)); // "room123" -> 123
            } else {
                liveNo = Long.parseLong(roomId); // "123" -> 123
            }
        } catch (NumberFormatException e) {
            log.warn("Invalid roomId format: {}", roomId);
            sendErrorMessage(session, "Invalid roomId format. Use liveNo (number) or room+liveNo format");
            return;
        }
        
        log.info("User {} joining live {} as {}", userId, liveNo, isBroadcaster ? "broadcaster" : "viewer");
        
        // 세션 정보 저장
        sessionToUserId.put(session.getId(), userId);
        sessionToRoomId.put(session.getId(), roomId);
        
        // 방 가져오기 또는 생성
        WebRTCRoom room = rooms.computeIfAbsent(roomId, WebRTCRoom::new);
        
        if (isBroadcaster) {
            // 이미 방송자가 있는 경우
            if (room.hasBroadcaster()) {
                sendErrorMessage(session, "Room already has a broadcaster");
                return;
            }
            room.setBroadcaster(userId, session);
            log.info("Broadcaster {} joined live {}", userId, liveNo);
        } else {
            // 1:1 연결을 위해 시청자 수 제한 (임시 해결책)
            if (room.getViewers().size() >= 1) {
                sendErrorMessage(session, "Room is full (1:1 mode)");
                return;
            }
            
            room.addViewer(userId, session);
            log.info("Viewer {} joined live {}", userId, liveNo);
            
            // 다른 사용자들에게 새 사용자 입장 알림
            notifyUserJoined(room, userId);
        }
        
        // 방 입장 성공 응답
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("viewers", new ArrayList<>(room.getViewers()));
        responseData.put("roomSize", room.getViewers().size() + (room.hasBroadcaster() ? 1 : 0));
        responseData.put("liveNo", liveNo);
        responseData.put("success", true);
        sendMessage(session, "room-joined", responseData);
    }
    
    private void handleOffer(WebSocketSession session, SignalingMessage message) {
        String roomId = sessionToRoomId.get(session.getId());
        WebRTCRoom room = rooms.get(roomId);
        
        if (room == null) {
            sendErrorMessage(session, "Room not found");
            return;
        }
        
        // 방송자만 offer를 보낼 수 있음
        if (!session.equals(room.getBroadcasterSession())) {
            sendErrorMessage(session, "Only broadcaster can send offer");
            return;
        }
        
        log.info("Broadcasting offer from {} to {} viewers", 
                sessionToUserId.get(session.getId()), room.getViewers().size());
        
        // 1:1 연결을 위해 첫 번째 시청자에게만 offer 전달 (임시 해결책)
        if (!room.getViewerSessions().isEmpty()) {
            WebSocketSession firstViewer = room.getViewerSessions().values().iterator().next();
            sendMessage(firstViewer, "offer", message.getData());
            log.info("Offer sent to first viewer: {}", sessionToUserId.get(firstViewer.getId()));
        }
    }
    
    private void handleAnswer(WebSocketSession session, SignalingMessage message) {
        String roomId = sessionToRoomId.get(session.getId());
        WebRTCRoom room = rooms.get(roomId);
        
        if (room == null) {
            sendErrorMessage(session, "Room not found");
            return;
        }
        
        String userId = sessionToUserId.get(session.getId());
        log.info("Answer received from viewer: {}", userId);
        
        // 방송자에게 answer 전달
        if (room.getBroadcasterSession() != null) {
            sendMessage(room.getBroadcasterSession(), "answer", message.getData());
            log.info("Answer forwarded to broadcaster");
        }
    }
    
    private void handleIceCandidate(WebSocketSession session, SignalingMessage message) {
        String roomId = sessionToRoomId.get(session.getId());
        WebRTCRoom room = rooms.get(roomId);
        
        if (room == null) {
            sendErrorMessage(session, "Room not found");
            return;
        }
        
        // 방송자인 경우 모든 시청자에게 전달
        if (session.equals(room.getBroadcasterSession())) {
            for (WebSocketSession viewerSession : room.getViewerSessions().values()) {
                sendMessage(viewerSession, "ice-candidate", message.getData());
            }
        } else {
            // 시청자인 경우 방송자에게만 전달
            if (room.getBroadcasterSession() != null) {
                sendMessage(room.getBroadcasterSession(), "ice-candidate", message.getData());
            }
        }
    }
    
    private void notifyUserJoined(WebRTCRoom room, String joinedUserId) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", joinedUserId);
        
        // 방송자에게 알림
        if (room.getBroadcasterSession() != null) {
            sendMessage(room.getBroadcasterSession(), "user-joined", data);
        }
        
        // 다른 시청자들에게 알림
        for (Map.Entry<String, WebSocketSession> entry : room.getViewerSessions().entrySet()) {
            if (!entry.getKey().equals(joinedUserId)) {
                sendMessage(entry.getValue(), "user-joined", data);
            }
        }
    }
    
    private void notifyUserLeft(WebRTCRoom room, String leftUserId) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", leftUserId);
        
        // 방송자에게 알림
        if (room.getBroadcasterSession() != null) {
            sendMessage(room.getBroadcasterSession(), "user-left", data);
        }
        
        // 다른 시청자들에게 알림
        for (WebSocketSession viewerSession : room.getViewerSessions().values()) {
            sendMessage(viewerSession, "user-left", data);
        }
    }
    
    public void handleSessionClosed(WebSocketSession session) {
        String sessionId = session.getId();
        String userId = sessionToUserId.get(sessionId);
        String roomId = sessionToRoomId.get(sessionId);
        
        if (userId == null || roomId == null) {
            return;
        }
        
        WebRTCRoom room = rooms.get(roomId);
        if (room != null) {
            // 방송자가 나간 경우
            if (session.equals(room.getBroadcasterSession())) {
                room.setBroadcaster(null, null);
                log.info("Broadcaster {} left room {}", userId, roomId);
                
                // 모든 시청자들에게 방송 종료 알림
                for (WebSocketSession viewerSession : room.getViewerSessions().values()) {
                    sendErrorMessage(viewerSession, "Broadcast ended");
                }
            } else {
                // 시청자가 나간 경우
                room.removeViewer(userId);
                log.info("Viewer {} left room {}", userId, roomId);
                notifyUserLeft(room, userId);
            }
            
            // 방이 비어있으면 제거
            if (room.isEmpty()) {
                rooms.remove(roomId);
                log.info("Room {} removed (empty)", roomId);
            }
        }
        
        // 세션 정보 정리
        sessionToUserId.remove(sessionId);
        sessionToRoomId.remove(sessionId);
    }
    
    private void sendMessage(WebSocketSession session, String type, Map<String, Object> data) {
        if (session.isOpen()) {
            try {
                Map<String, Object> message = new HashMap<>();
                message.put("type", type);
                message.put("data", data);
                
                String messageJson = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(messageJson));
            } catch (IOException e) {
                log.error("Failed to send message to session {}", session.getId(), e);
            }
        }
    }
    
    private void sendErrorMessage(WebSocketSession session, String errorMessage) {
        Map<String, Object> data = new HashMap<>();
        data.put("message", errorMessage);
        sendMessage(session, "error", data);
    }
    
    public int getRoomCount() {
        return rooms.size();
    }
    
    public int getTotalUsers() {
        return rooms.values().stream()
                .mapToInt(room -> (room.hasBroadcaster() ? 1 : 0) + room.getViewers().size())
                .sum();
    }
    
    public int getRoomUserCount(String roomId) {
        WebRTCRoom room = rooms.get(roomId);
        if (room == null) {
            return 0;
        }
        return (room.hasBroadcaster() ? 1 : 0) + room.getViewers().size();
    }
    
    public boolean hasActiveBroadcaster(String roomId) {
        WebRTCRoom room = rooms.get(roomId);
        return room != null && room.hasBroadcaster();
    }
}
