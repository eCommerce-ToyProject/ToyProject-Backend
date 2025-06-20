package com.idrsys.toyprojectbackend.service;

import com.idrsys.toyprojectbackend.dto.chat.LiveRoomCreateDto;
import com.idrsys.toyprojectbackend.dto.chat.LiveRoomDto;
import com.idrsys.toyprojectbackend.dto.chat.LiveRoomUpdateDto;
import com.idrsys.toyprojectbackend.entity.LiveRoom;
import com.idrsys.toyprojectbackend.entity.Member;
import com.idrsys.toyprojectbackend.repository.chat.LiveRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LiveRoomService {

    private final LiveRoomRepository liveRoomRepository;
    private final MemberService memberService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final LiveScheduleService liveScheduleService;

    private static final String LIVE_ROOM_USERS_KEY = "live:room:users:";

    /**
     * 라이브 방송 생성
     */
    public LiveRoomDto createLiveRoom(LiveRoomCreateDto createDto) {
        // 호스트 회원 확인 (memberId로 조회)
        Member host = memberService.findById(createDto.getHostMemberId())
                .orElseThrow(() -> new IllegalArgumentException("호스트 회원을 찾을 수 없습니다."));

        Integer hostMemNo = host.getMemNo();

        // 동일한 호스트의 진행 중인 라이브 방송 확인
        List<LiveRoom> activeLiveRooms = liveRoomRepository.findByHostMemNoAndStatus(
                hostMemNo, LiveRoom.LiveStatus.LIVE);
        
        if (!activeLiveRooms.isEmpty()) {
            throw new IllegalStateException("이미 진행 중인 라이브 방송이 있습니다.");
        }

        // 라이브 방송 생성
        LiveRoom liveRoom = LiveRoom.builder()
                .hostMemNo(hostMemNo)
                .catCd(createDto.getCatCd())
                .title(createDto.getTitle())
                .startTime(createDto.getStartTime())
                .endTime(createDto.getEndTime())
                .status(LiveRoom.LiveStatus.SCHEDULED)
                .build();

        LiveRoom savedLiveRoom = liveRoomRepository.save(liveRoom);

        // 🎯 자동 시작 스케줄 등록 (autoStart가 true인 경우만)
        if (Boolean.TRUE.equals(createDto.getAutoStart()) && createDto.getStartTime().isAfter(LocalDateTime.now())) {
            liveScheduleService.scheduleAutoStart(savedLiveRoom.getLiveNo(), createDto.getStartTime());
            log.info("라이브 방송 자동 시작 스케줄 등록: liveNo={}, startTime={}", 
                savedLiveRoom.getLiveNo(), createDto.getStartTime());
        }

        log.info("Live room created: liveNo={}, host={}, title={}", 
                savedLiveRoom.getLiveNo(), host.getUsername(), savedLiveRoom.getTitle());

        return convertToDto(savedLiveRoom, host.getUsername(), host.getId(), null);
    }

    /**
     * 라이브 방송 시작
     */
    public LiveRoomDto startLiveRoom(Long liveNo, String hostMemberId) {
        LiveRoom liveRoom = liveRoomRepository.findById(liveNo)
                .orElseThrow(() -> new IllegalArgumentException("라이브 방송을 찾을 수 없습니다."));

        // 호스트 회원 확인
        Member host = memberService.findById(hostMemberId)
                .orElseThrow(() -> new IllegalArgumentException("호스트 회원을 찾을 수 없습니다."));

        // 호스트 권한 확인
        if (!liveRoom.getHostMemNo().equals(host.getMemNo())) {
            throw new IllegalArgumentException("라이브 방송을 시작할 권한이 없습니다.");
        }

        // 상태 확인
        if (liveRoom.getStatus() != LiveRoom.LiveStatus.SCHEDULED) {
            throw new IllegalStateException("예정된 상태의 라이브 방송만 시작할 수 있습니다.");
        }

        // 상태를 LIVE로 변경
        liveRoom.setStatus(LiveRoom.LiveStatus.LIVE);
        liveRoom.setStartTime(LocalDateTime.now());
        
        LiveRoom updatedLiveRoom = liveRoomRepository.save(liveRoom);

        log.info("Live room started: liveNo={}, hostMemberId={}", liveNo, hostMemberId);

        return convertToDto(updatedLiveRoom, host.getUsername(), host.getId(), null);
    }

    /**
     * 라이브 방송 종료
     */
    public LiveRoomDto endLiveRoom(Long liveNo, String hostMemberId) {
        LiveRoom liveRoom = liveRoomRepository.findById(liveNo)
                .orElseThrow(() -> new IllegalArgumentException("라이브 방송을 찾을 수 없습니다."));

        // 호스트 회원 확인
        Member host = memberService.findById(hostMemberId)
                .orElseThrow(() -> new IllegalArgumentException("호스트 회원을 찾을 수 없습니다."));

        // 호스트 권한 확인
        if (!liveRoom.getHostMemNo().equals(host.getMemNo())) {
            throw new IllegalArgumentException("라이브 방송을 종료할 권한이 없습니다.");
        }

        // 상태 확인
        if (liveRoom.getStatus() != LiveRoom.LiveStatus.LIVE) {
            throw new IllegalStateException("진행 중인 라이브 방송만 종료할 수 있습니다.");
        }

        // 상태를 ENDED로 변경
        liveRoom.setStatus(LiveRoom.LiveStatus.ENDED);
        liveRoom.setEndTime(LocalDateTime.now());
        
        LiveRoom updatedLiveRoom = liveRoomRepository.save(liveRoom);

        // Redis에서 접속자 정보 정리
        String roomKey = LIVE_ROOM_USERS_KEY + liveNo;
        redisTemplate.delete(roomKey);

        log.info("Live room ended: liveNo={}, hostMemberId={}", liveNo, hostMemberId);

        return convertToDto(updatedLiveRoom, host.getUsername(), host.getId(), null);
    }

    /**
     * 라이브 방송 정보 수정
     */
    public LiveRoomDto updateLiveRoom(Long liveNo, String hostMemberId, LiveRoomUpdateDto updateDto) {
        LiveRoom liveRoom = liveRoomRepository.findById(liveNo)
                .orElseThrow(() -> new IllegalArgumentException("라이브 방송을 찾을 수 없습니다."));

        // 호스트 회원 확인
        Member host = memberService.findById(hostMemberId)
                .orElseThrow(() -> new IllegalArgumentException("호스트 회원을 찾을 수 없습니다."));

        // 호스트 권한 확인
        if (!liveRoom.getHostMemNo().equals(host.getMemNo())) {
            throw new IllegalArgumentException("라이브 방송을 수정할 권한이 없습니다.");
        }

        boolean scheduleChanged = false;

        // 진행 중인 방송은 제목만 수정 가능
        if (liveRoom.getStatus() == LiveRoom.LiveStatus.LIVE) {
            if (updateDto.getTitle() != null) {
                liveRoom.setTitle(updateDto.getTitle());
            }
        } else if (liveRoom.getStatus() == LiveRoom.LiveStatus.SCHEDULED) {
            // 예정된 방송은 모든 정보 수정 가능
            if (updateDto.getTitle() != null) {
                liveRoom.setTitle(updateDto.getTitle());
            }
            if (updateDto.getStartTime() != null) {
                liveRoom.setStartTime(updateDto.getStartTime());
                scheduleChanged = true;
            }
            if (updateDto.getEndTime() != null) {
                liveRoom.setEndTime(updateDto.getEndTime());
            }
            if (updateDto.getStatus() != null) {
                liveRoom.setStatus(updateDto.getStatus());
                if (updateDto.getStatus() == LiveRoom.LiveStatus.CANCELLED) {
                    // 취소된 경우 스케줄도 취소
                    liveScheduleService.cancelScheduledTask(liveNo);
                }
            }
        } else {
            throw new IllegalStateException("종료된 라이브 방송은 수정할 수 없습니다.");
        }

        LiveRoom updatedLiveRoom = liveRoomRepository.save(liveRoom);

        // 🎯 시작 시간이 변경된 경우 스케줄 재등록
        if (scheduleChanged && liveRoom.getStatus() == LiveRoom.LiveStatus.SCHEDULED) {
            liveScheduleService.scheduleAutoStart(liveNo, liveRoom.getStartTime());
            log.info("라이브 방송 스케줄 재등록: liveNo={}, newStartTime={}", liveNo, liveRoom.getStartTime());
        }

        return convertToDto(updatedLiveRoom, host.getUsername(), host.getId(), null);
    }

    /**
     * 라이브 방송 상세 조회
     */
    @Transactional(readOnly = true)
    public LiveRoomDto getLiveRoom(Long liveNo) {
        LiveRoom liveRoom = liveRoomRepository.findById(liveNo)
                .orElseThrow(() -> new IllegalArgumentException("라이브 방송을 찾을 수 없습니다."));

        Member host = memberService.findById(liveRoom.getHostMemNo()).orElse(null);
        Long currentUserCount = getCurrentUserCount(liveNo);
        
        return convertToDto(liveRoom, 
                host != null ? host.getUsername() : null, 
                host != null ? host.getId() : null, 
                currentUserCount);
    }

    /**
     * 현재 라이브 중인 방송 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<LiveRoomDto> getLiveRooms(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startTime"));
        Page<LiveRoom> liveRoomPage = liveRoomRepository.findByStatusOrderByStartTimeDesc(
                LiveRoom.LiveStatus.LIVE, pageable);

        return liveRoomPage.map(liveRoom -> {
            Member host = memberService.findById(liveRoom.getHostMemNo()).orElse(null);
            Long currentUserCount = getCurrentUserCount(liveRoom.getLiveNo());
            return convertToDto(liveRoom, 
                    host != null ? host.getUsername() : null, 
                    host != null ? host.getId() : null, 
                    currentUserCount);
        });
    }

    /**
     * 특정 호스트의 라이브 방송 목록 조회
     */
    @Transactional(readOnly = true)
    public List<LiveRoomDto> getLiveRoomsByHost(String hostMemberId) {
        // 호스트 회원 확인
        Member host = memberService.findById(hostMemberId)
                .orElseThrow(() -> new IllegalArgumentException("호스트 회원을 찾을 수 없습니다."));

        List<LiveRoom> liveRooms = liveRoomRepository.findByHostMemNoOrderByStartTimeDesc(host.getMemNo());
        
        return liveRooms.stream()
                .map(liveRoom -> {
                    Long currentUserCount = liveRoom.getStatus() == LiveRoom.LiveStatus.LIVE 
                            ? getCurrentUserCount(liveRoom.getLiveNo()) : 0L;
                    return convertToDto(liveRoom, host.getUsername(), host.getId(), currentUserCount);
                })
                .collect(Collectors.toList());
    }

    /**
     * 카테고리별 라이브 방송 목록 조회
     */
    @Transactional(readOnly = true)
    public List<LiveRoomDto> getLiveRoomsByCategory(Integer catCd) {
        List<LiveRoom> liveRooms = liveRoomRepository.findByCatCdAndStatusOrderByStartTimeDesc(
                catCd, LiveRoom.LiveStatus.LIVE);
        
        return liveRooms.stream()
                .map(liveRoom -> {
                    Member host = memberService.findById(liveRoom.getHostMemNo()).orElse(null);
                    Long currentUserCount = getCurrentUserCount(liveRoom.getLiveNo());
                    return convertToDto(liveRoom, 
                            host != null ? host.getUsername() : null, 
                            host != null ? host.getId() : null, 
                            currentUserCount);
                })
                .collect(Collectors.toList());
    }

    /**
     * 라이브 방송 삭제 (호스트만 가능)
     */
    public void deleteLiveRoom(Long liveNo, String hostMemberId) {
        LiveRoom liveRoom = liveRoomRepository.findById(liveNo)
                .orElseThrow(() -> new IllegalArgumentException("라이브 방송을 찾을 수 없습니다."));

        // 호스트 회원 확인
        Member host = memberService.findById(hostMemberId)
                .orElseThrow(() -> new IllegalArgumentException("호스트 회원을 찾을 수 없습니다."));

        // 호스트 권한 확인
        if (!liveRoom.getHostMemNo().equals(host.getMemNo())) {
            throw new IllegalArgumentException("라이브 방송을 삭제할 권한이 없습니다.");
        }

        // 진행 중인 방송은 삭제 불가
        if (liveRoom.getStatus() == LiveRoom.LiveStatus.LIVE) {
            throw new IllegalStateException("진행 중인 라이브 방송은 삭제할 수 없습니다.");
        }

        // 🎯 스케줄된 작업 취소
        liveScheduleService.cancelScheduledTask(liveNo);

        liveRoomRepository.delete(liveRoom);

        // Redis에서 관련 정보 정리
        String roomKey = LIVE_ROOM_USERS_KEY + liveNo;
        redisTemplate.delete(roomKey);

        log.info("Live room deleted: liveNo={}, hostMemberId={}", liveNo, hostMemberId);
    }

    /**
     * 현재 접속자 수 조회
     */
    @Transactional(readOnly = true)
    public Long getCurrentUserCount(Long liveNo) {
        String roomKey = LIVE_ROOM_USERS_KEY + liveNo;
        Set<Object> users = redisTemplate.opsForSet().members(roomKey);
        return users != null ? (long) users.size() : 0L;
    }

    /**
     * LiveRoom 엔티티를 DTO로 변환
     */
    private LiveRoomDto convertToDto(LiveRoom liveRoom, String hostMemName, String hostMemberId, Long currentUserCount) {
        return LiveRoomDto.builder()
                .liveNo(liveRoom.getLiveNo())
                .hostMemNo(liveRoom.getHostMemNo())
                .hostMemberId(hostMemberId)
                .hostMemName(hostMemName)
                .catCd(liveRoom.getCatCd())
                .categoryName(null) // 필요시 Category 엔티티 조회하여 설정
                .title(liveRoom.getTitle())
                .startTime(liveRoom.getStartTime())
                .endTime(liveRoom.getEndTime())
                .status(liveRoom.getStatus())
                .createdAt(liveRoom.getCreatedAt())
                .updatedAt(liveRoom.getUpdatedAt())
                .currentUserCount(currentUserCount != null ? currentUserCount : 0L)
                .build();
    }
}
