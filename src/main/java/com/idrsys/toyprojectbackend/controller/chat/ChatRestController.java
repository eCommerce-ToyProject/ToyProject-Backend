package com.idrsys.toyprojectbackend.controller.chat;

import com.idrsys.toyprojectbackend.dto.chat.ChatMessageDto;
import com.idrsys.toyprojectbackend.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chat API", description = "라이브 채팅 관련 REST API")
public class ChatRestController {

    private final ChatService chatService;

    /**
     * 채팅 기록 조회 (페이징)
     */
    @GetMapping("/history/{liveNo}")
    @Operation(summary = "채팅 기록 조회", description = "특정 라이브 방송의 채팅 기록을 페이징으로 조회합니다.")
    public ResponseEntity<Page<ChatMessageDto>> getChatHistory(
            @Parameter(description = "라이브 방송 번호") @PathVariable Long liveNo,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "50") int size) {
        
        Page<ChatMessageDto> chatHistory = chatService.getChatHistory(liveNo, page, size);
        return ResponseEntity.ok(chatHistory);
    }

    /**
     * 특정 시간 이후 채팅 메시지 조회
     */
    @GetMapping("/after/{liveNo}")
    @Operation(summary = "특정 시간 이후 채팅 조회", description = "특정 시간 이후의 채팅 메시지를 조회합니다.")
    public ResponseEntity<List<ChatMessageDto>> getChatMessagesAfter(
            @Parameter(description = "라이브 방송 번호") @PathVariable Long liveNo,
            @Parameter(description = "기준 시간 (yyyy-MM-dd'T'HH:mm:ss)") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime afterTime) {
        
        List<ChatMessageDto> messages = chatService.getChatMessagesAfter(liveNo, afterTime);
        return ResponseEntity.ok(messages);
    }

    /**
     * 현재 접속자 수 조회
     */
    @GetMapping("/usercount/{liveNo}")
    @Operation(summary = "현재 접속자 수 조회", description = "라이브 방송의 현재 접속자 수를 조회합니다.")
    public ResponseEntity<Long> getCurrentUserCount(
            @Parameter(description = "라이브 방송 번호") @PathVariable Long liveNo) {
        
        long userCount = chatService.getCurrentUserCount(liveNo);
        return ResponseEntity.ok(userCount);
    }

    /**
     * 현재 접속자 목록 조회
     */
    @GetMapping("/users/{liveNo}")
    @Operation(summary = "현재 접속자 목록 조회", description = "라이브 방송의 현재 접속자 목록을 조회합니다.")
    public ResponseEntity<List<Integer>> getCurrentUsers(
            @Parameter(description = "라이브 방송 번호") @PathVariable Long liveNo) {
        
        List<Integer> users = chatService.getCurrentUsers(liveNo);
        return ResponseEntity.ok(users);
    }
}
