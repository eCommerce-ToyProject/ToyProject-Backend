package com.idrsys.toyprojectbackend.dto.webrtc;

import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Data
public class WebRTCRoom {
    private String roomId;
    private String broadcasterId;
    private WebSocketSession broadcasterSession;
    private Set<String> viewers = new CopyOnWriteArraySet<>();
    private Map<String, WebSocketSession> viewerSessions = new ConcurrentHashMap<>();
    
    public WebRTCRoom(String roomId) {
        this.roomId = roomId;
    }
    
    public void setBroadcaster(String userId, WebSocketSession session) {
        this.broadcasterId = userId;
        this.broadcasterSession = session;
    }
    
    public void addViewer(String userId, WebSocketSession session) {
        this.viewers.add(userId);
        this.viewerSessions.put(userId, session);
    }
    
    public void removeViewer(String userId) {
        this.viewers.remove(userId);
        this.viewerSessions.remove(userId);
    }
    
    public boolean hasBroadcaster() {
        return broadcasterId != null && broadcasterSession != null;
    }
    
    public boolean isEmpty() {
        return !hasBroadcaster() && viewers.isEmpty();
    }
}
