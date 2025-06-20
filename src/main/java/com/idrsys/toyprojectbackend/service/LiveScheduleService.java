package com.idrsys.toyprojectbackend.service;

import com.idrsys.toyprojectbackend.entity.LiveRoom;
import com.idrsys.toyprojectbackend.repository.chat.LiveRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiveScheduleService {
    
    private final TaskScheduler taskScheduler;
    private final LiveRoomRepository liveRoomRepository;
    
    // 스케줄된 작업들을 저장하는 맵 (liveNo -> ScheduledFuture)
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    
    /**
     * 라이브 방송 자동 시작 스케줄 등록
     */
    public void scheduleAutoStart(Long liveNo, LocalDateTime startTime) {
        // 이미 스케줄된 작업이 있다면 취소
        cancelScheduledTask(liveNo);
        
        // 시작 시간이 현재 시간보다 이후인 경우만 스케줄링
        if (startTime.isAfter(LocalDateTime.now())) {
            Instant startInstant = startTime.atZone(ZoneId.systemDefault()).toInstant();
            
            ScheduledFuture<?> scheduledTask = taskScheduler.schedule(
                () -> executeAutoStart(liveNo),
                startInstant
            );
            
            scheduledTasks.put(liveNo, scheduledTask);
            log.info("라이브 방송 자동 시작 스케줄 등록: liveNo={}, startTime={}", liveNo, startTime);
        } else {
            log.warn("과거 시간으로 스케줄 등록 시도: liveNo={}, startTime={}", liveNo, startTime);
        }
    }
    
    /**
     * 자동 시작 실행
     */
    @Transactional
    public void executeAutoStart(Long liveNo) {
        try {
            log.info("라이브 방송 자동 시작 실행: liveNo={}", liveNo);
            
            // 라이브 방송 정보 조회
            LiveRoom liveRoom = liveRoomRepository.findById(liveNo)
                .orElse(null);
            
            if (liveRoom == null) {
                log.error("라이브 방송을 찾을 수 없음: liveNo={}", liveNo);
                return;
            }
            
            // 현재 상태가 SCHEDULED인 경우만 자동 시작
            if (liveRoom.getStatus() == LiveRoom.LiveStatus.SCHEDULED) {
                // 상태를 LIVE로 변경
                liveRoom.setStatus(LiveRoom.LiveStatus.LIVE);
                liveRoom.setStartTime(LocalDateTime.now());
                liveRoomRepository.save(liveRoom);
                
                log.info("라이브 방송 자동 시작 완료: liveNo={}, hostMemNo={}", 
                    liveNo, liveRoom.getHostMemNo());
                
                // 자동 종료 스케줄 등록 (종료 시간이 설정된 경우)
                if (liveRoom.getEndTime() != null) {
                    scheduleAutoEnd(liveNo, liveRoom.getEndTime());
                }
            } else {
                log.warn("라이브 방송이 이미 시작되었거나 취소됨: liveNo={}, status={}", 
                    liveNo, liveRoom.getStatus());
            }
            
        } catch (Exception e) {
            log.error("라이브 방송 자동 시작 실패: liveNo={}", liveNo, e);
        } finally {
            // 완료된 스케줄 제거
            scheduledTasks.remove(liveNo);
        }
    }
    
    /**
     * 라이브 방송 자동 종료 스케줄 등록
     */
    public void scheduleAutoEnd(Long liveNo, LocalDateTime endTime) {
        if (endTime.isAfter(LocalDateTime.now())) {
            Instant endInstant = endTime.atZone(ZoneId.systemDefault()).toInstant();
            
            ScheduledFuture<?> scheduledTask = taskScheduler.schedule(
                () -> executeAutoEnd(liveNo),
                endInstant
            );
            
            // 종료 스케줄은 별도 키로 관리 (liveNo + "_end")
            scheduledTasks.put(liveNo * -1, scheduledTask);
            log.info("라이브 방송 자동 종료 스케줄 등록: liveNo={}, endTime={}", liveNo, endTime);
        }
    }
    
    /**
     * 자동 종료 실행
     */
    @Transactional
    public void executeAutoEnd(Long liveNo) {
        try {
            log.info("라이브 방송 자동 종료 실행: liveNo={}", liveNo);
            
            LiveRoom liveRoom = liveRoomRepository.findById(liveNo)
                .orElse(null);
            
            if (liveRoom != null && liveRoom.getStatus() == LiveRoom.LiveStatus.LIVE) {
                // 상태를 ENDED로 변경
                liveRoom.setStatus(LiveRoom.LiveStatus.ENDED);
                liveRoom.setEndTime(LocalDateTime.now());
                liveRoomRepository.save(liveRoom);
                
                log.info("라이브 방송 자동 종료 완료: liveNo={}", liveNo);
                
            } else {
                log.warn("라이브 방송이 이미 종료되었거나 진행 중이 아님: liveNo={}", liveNo);
            }
            
        } catch (Exception e) {
            log.error("라이브 방송 자동 종료 실패: liveNo={}", liveNo, e);
        } finally {
            scheduledTasks.remove(liveNo * -1);
        }
    }
    
    /**
     * 스케줄된 작업 취소
     */
    public void cancelScheduledTask(Long liveNo) {
        // 시작 스케줄 취소
        ScheduledFuture<?> startTask = scheduledTasks.remove(liveNo);
        if (startTask != null && !startTask.isDone()) {
            startTask.cancel(false);
            log.info("라이브 방송 자동 시작 스케줄 취소: liveNo={}", liveNo);
        }
        
        // 종료 스케줄 취소
        ScheduledFuture<?> endTask = scheduledTasks.remove(liveNo * -1);
        if (endTask != null && !endTask.isDone()) {
            endTask.cancel(false);
            log.info("라이브 방송 자동 종료 스케줄 취소: liveNo={}", liveNo);
        }
    }
    
    /**
     * 스케줄 정보 조회
     */
    public boolean isScheduled(Long liveNo) {
        ScheduledFuture<?> task = scheduledTasks.get(liveNo);
        return task != null && !task.isDone();
    }
    
    /**
     * 모든 활성 스케줄 수 조회
     */
    public int getActiveScheduleCount() {
        return (int) scheduledTasks.values().stream()
            .filter(task -> !task.isDone())
            .count();
    }
}
