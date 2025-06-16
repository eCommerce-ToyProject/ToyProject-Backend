package com.idrsys.toyprojectbackend.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {
    
    private Long chatNo;
    private Long liveNo;
    private Integer memNo;
    private String memName;
    private String message;
    private LocalDateTime sendTime;
    private MessageType type;
    
    public enum MessageType {
        CHAT,       // 일반 채팅
        JOIN,       // 입장
        LEAVE,      // 퇴장
        SYSTEM      // 시스템 메시지
    }
}
