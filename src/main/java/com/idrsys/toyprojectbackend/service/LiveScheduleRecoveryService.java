package com.idrsys.toyprojectbackend.service;

import com.idrsys.toyprojectbackend.entity.LiveRoom;
import com.idrsys.toyprojectbackend.repository.chat.LiveRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiveScheduleRecoveryService {
    
    private final LiveRoomRepository liveRoomRepository;
    private final LiveScheduleService liveScheduleService;
    
    /**
     * 애플리케이션 시작 시 기존 스케줄 복구
     */
    @EventListener(ApplicationReadyEvent.class)
    public void recoverSchedules() {
        log.info("라이브 방송 스케줄 복구 시작...");
        
        try {
            // SCHEDULED 상태이면서 시작 시간이 미래인 방송들 조회
            List<LiveRoom> scheduledRooms = liveRoomRepository
                .findByStatusAndStartTimeAfter(
                    LiveRoom.LiveStatus.SCHEDULED, 
                    LocalDateTime.now()
                );
            
            int recoveredCount = 0;
            
            for (LiveRoom liveRoom : scheduledRooms) {
                try {
                    // 자동 시작 스케줄 등록
                    liveScheduleService.scheduleAutoStart(
                        liveRoom.getLiveNo(), 
                        liveRoom.getStartTime()
                    );
                    recoveredCount++;
                    
                    log.debug("스케줄 복구: liveNo={}, startTime={}", 
                        liveRoom.getLiveNo(), liveRoom.getStartTime());
                    
                } catch (Exception e) {
                    log.error("스케줄 복구 실패: liveNo={}", liveRoom.getLiveNo(), e);
                }
            }
            
            log.info("라이브 방송 스케줄 복구 완료: 총 {}개 복구", recoveredCount);
            
        } catch (Exception e) {
            log.error("라이브 방송 스케줄 복구 중 오류 발생", e);
        }
    }
}
