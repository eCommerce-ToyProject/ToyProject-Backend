package com.idrsys.toyprojectbackend.exception;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleNullPointerException(IllegalStateException e) {
        return new ResponseEntity<>(Map.of("errors", e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * 채팅 관련 예외 처리
     */
    @ExceptionHandler(ChatException.class)
    public ResponseEntity<Map<String, Object>> handleChatException(ChatException e) {
        log.error("Chat exception occurred: {}", e.getMessage(), e);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "CHAT_ERROR");
        errorResponse.put("message", e.getMessage());
        errorResponse.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Illegal argument exception: {}", e.getMessage(), e);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "INVALID_ARGUMENT");
        errorResponse.put("message", e.getMessage());
        errorResponse.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> handleBindException(BindException e) {
        log.error("Validation exception: {}", e.getMessage(), e);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "VALIDATION_ERROR");
        errorResponse.put("message", "입력값 검증에 실패했습니다.");
        errorResponse.put("details", e.getBindingResult().getFieldErrors());
        errorResponse.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * WebSocket 메시지 예외 처리
     */
    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public Map<String, Object> handleWebSocketException(Exception e) {
        log.error("WebSocket exception occurred: {}", e.getMessage(), e);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "WEBSOCKET_ERROR");
        errorResponse.put("message", e.getMessage());
        errorResponse.put("timestamp", LocalDateTime.now());
        
        return errorResponse;
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, List<String>>> handleRuntimeException(RuntimeException e) {
        return new ResponseEntity<>(Map.of("errors", List.of(e.getMessage())), HttpStatus.BAD_REQUEST);
    }
}
