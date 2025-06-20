package com.idrsys.toyprojectbackend.dto.chat;

import com.idrsys.toyprojectbackend.entity.LiveRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveRoomDto {
    
    private Long liveNo;
    private Integer hostMemNo;  // DB 저장용 (내부적으로 변환)
    private String hostMemberId; // 외부 API용
    private String hostMemName;
    private Integer catCd;
    private String categoryName;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LiveRoom.LiveStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long currentUserCount;
}
