package com.idrsys.toyprojectbackend.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiveRoomCreateDto {
    
    @NotNull(message = "호스트 회원 번호는 필수입니다")
    private Integer hostMemNo;
    
    @NotNull(message = "카테고리 코드는 필수입니다")
    private Integer catCd;
    
    @NotBlank(message = "방송 제목은 필수입니다")
    private String title;
    
    @NotNull(message = "방송 시작 시간은 필수입니다")
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
}
