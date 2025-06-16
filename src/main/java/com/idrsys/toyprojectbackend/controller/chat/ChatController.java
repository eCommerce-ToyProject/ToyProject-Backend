package com.idrsys.toyprojectbackend.controller.chat;

import com.idrsys.toyprojectbackend.dto.chat.ChatMessageDto;
import com.idrsys.toyprojectbackend.dto.chat.ChatRequestDto;
import com.idrsys.toyprojectbackend.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    /**
     * 채팅 메시지 전송
     * 클라이언트에서 /app/chat.sendMessage/{liveNo}로 메시지 전송
     */
    @MessageMapping("/chat.sendMessage/{liveNo}")
    @SendTo("/topic/live/{liveNo}")
    public ChatMessageDto sendMessage(
            @DestinationVariable Long liveNo,
            @Payload @Validated ChatRequestDto request) {
        
        log.info("Received chat message: liveNo={}, memNo={}, message={}", 
                liveNo, request.getMemNo(), request.getMessage());
        
        // 요청 DTO에 라이브 번호 설정
        request.setLiveNo(liveNo);
        
        return chatService.sendMessage(request);
    }

    /**
     * 라이브 방송 입장
     * 클라이언트에서 /app/chat.join/{liveNo}로 입장 요청
     */
    @MessageMapping("/chat.join/{liveNo}")
    @SendTo("/topic/live/{liveNo}")
    public ChatMessageDto joinLiveRoom(
            @DestinationVariable Long liveNo,
            @Payload Integer memNo,
            SimpMessageHeaderAccessor headerAccessor) {
        
        log.info("User joining live room: liveNo={}, memNo={}", liveNo, memNo);
        
        // 세션에 사용자 정보 저장 (퇴장 시 사용)
        headerAccessor.getSessionAttributes().put("memNo", memNo);
        headerAccessor.getSessionAttributes().put("liveNo", liveNo);
        
        return chatService.joinLiveRoom(liveNo, memNo);
    }

    /**
     * 라이브 방송 퇴장
     * 클라이언트에서 /app/chat.leave/{liveNo}로 퇴장 요청
     */
    @MessageMapping("/chat.leave/{liveNo}")
    @SendTo("/topic/live/{liveNo}")
    public ChatMessageDto leaveLiveRoom(
            @DestinationVariable Long liveNo,
            @Payload Integer memNo) {
        
        log.info("User leaving live room: liveNo={}, memNo={}", liveNo, memNo);
        
        return chatService.leaveLiveRoom(liveNo, memNo);
    }
}
