package com.idrsys.toyprojectbackend.dto.chat;

import com.idrsys.toyprojectbackend.entity.LiveRoom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiveRoomUpdateDto {
    
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LiveRoom.LiveStatus status;
}
