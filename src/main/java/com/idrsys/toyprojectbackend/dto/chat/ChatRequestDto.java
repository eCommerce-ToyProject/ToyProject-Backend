package com.idrsys.toyprojectbackend.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDto {
    
    @NotNull(message = "라이브 방송 번호는 필수입니다")
    private Long liveNo;
    
    @NotNull(message = "회원 번호는 필수입니다")
    private Integer memNo;
    
    @NotBlank(message = "메시지는 필수입니다")
    private String message;
    
    private ChatMessageDto.MessageType type = ChatMessageDto.MessageType.CHAT;
}
