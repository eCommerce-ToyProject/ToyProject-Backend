package com.idrsys.toyprojectbackend.controller.chat;

import com.idrsys.toyprojectbackend.dto.chat.LiveRoomCreateDto;
import com.idrsys.toyprojectbackend.dto.chat.LiveRoomDto;
import com.idrsys.toyprojectbackend.dto.chat.LiveRoomUpdateDto;
import com.idrsys.toyprojectbackend.service.LiveRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/live")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Live Room API", description = "라이브 방송 관리 API")
public class LiveRoomController {

    private final LiveRoomService liveRoomService;

    /**
     * 라이브 방송 생성
     */
    @PostMapping
    @Operation(summary = "라이브 방송 생성", description = "새로운 라이브 방송을 생성합니다.")
    public ResponseEntity<LiveRoomDto> createLiveRoom(
            @RequestBody @Validated LiveRoomCreateDto createDto) {
        
        LiveRoomDto liveRoom = liveRoomService.createLiveRoom(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(liveRoom);
    }

    /**
     * 라이브 방송 시작
     */
    @PostMapping("/{liveNo}/start")
    @Operation(summary = "라이브 방송 시작", description = "예정된 라이브 방송을 시작합니다.")
    public ResponseEntity<LiveRoomDto> startLiveRoom(
            @Parameter(description = "라이브 방송 번호") @PathVariable Long liveNo,
            @Parameter(description = "호스트 회원 번호") @RequestParam Integer hostMemNo) {
        
        LiveRoomDto liveRoom = liveRoomService.startLiveRoom(liveNo, hostMemNo);
        return ResponseEntity.ok(liveRoom);
    }

    /**
     * 라이브 방송 종료
     */
    @PostMapping("/{liveNo}/end")
    @Operation(summary = "라이브 방송 종료", description = "진행 중인 라이브 방송을 종료합니다.")
    public ResponseEntity<LiveRoomDto> endLiveRoom(
            @Parameter(description = "라이브 방송 번호") @PathVariable Long liveNo,
            @Parameter(description = "호스트 회원 번호") @RequestParam Integer hostMemNo) {
        
        LiveRoomDto liveRoom = liveRoomService.endLiveRoom(liveNo, hostMemNo);
        return ResponseEntity.ok(liveRoom);
    }

    /**
     * 라이브 방송 정보 수정
     */
    @PutMapping("/{liveNo}")
    @Operation(summary = "라이브 방송 정보 수정", description = "라이브 방송 정보를 수정합니다.")
    public ResponseEntity<LiveRoomDto> updateLiveRoom(
            @Parameter(description = "라이브 방송 번호") @PathVariable Long liveNo,
            @Parameter(description = "호스트 회원 번호") @RequestParam Integer hostMemNo,
            @RequestBody LiveRoomUpdateDto updateDto) {
        
        LiveRoomDto liveRoom = liveRoomService.updateLiveRoom(liveNo, hostMemNo, updateDto);
        return ResponseEntity.ok(liveRoom);
    }

    /**
     * 라이브 방송 상세 조회
     */
    @GetMapping("/{liveNo}")
    @Operation(summary = "라이브 방송 상세 조회", description = "특정 라이브 방송의 상세 정보를 조회합니다.")
    public ResponseEntity<LiveRoomDto> getLiveRoom(
            @Parameter(description = "라이브 방송 번호") @PathVariable Long liveNo) {
        
        LiveRoomDto liveRoom = liveRoomService.getLiveRoom(liveNo);
        return ResponseEntity.ok(liveRoom);
    }

    /**
     * 현재 라이브 중인 방송 목록 조회
     */
    @GetMapping
    @Operation(summary = "라이브 방송 목록 조회", description = "현재 진행 중인 라이브 방송 목록을 조회합니다.")
    public ResponseEntity<Page<LiveRoomDto>> getLiveRooms(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        
        Page<LiveRoomDto> liveRooms = liveRoomService.getLiveRooms(page, size);
        return ResponseEntity.ok(liveRooms);
    }

    /**
     * 특정 호스트의 라이브 방송 목록 조회
     */
    @GetMapping("/host/{hostMemNo}")
    @Operation(summary = "호스트별 라이브 방송 조회", description = "특정 호스트의 라이브 방송 목록을 조회합니다.")
    public ResponseEntity<List<LiveRoomDto>> getLiveRoomsByHost(
            @Parameter(description = "호스트 회원 번호") @PathVariable Integer hostMemNo) {
        
        List<LiveRoomDto> liveRooms = liveRoomService.getLiveRoomsByHost(hostMemNo);
        return ResponseEntity.ok(liveRooms);
    }

    /**
     * 카테고리별 라이브 방송 목록 조회
     */
    @GetMapping("/category/{catCd}")
    @Operation(summary = "카테고리별 라이브 방송 조회", description = "특정 카테고리의 라이브 방송 목록을 조회합니다.")
    public ResponseEntity<List<LiveRoomDto>> getLiveRoomsByCategory(
            @Parameter(description = "카테고리 코드") @PathVariable Integer catCd) {
        
        List<LiveRoomDto> liveRooms = liveRoomService.getLiveRoomsByCategory(catCd);
        return ResponseEntity.ok(liveRooms);
    }

    /**
     * 라이브 방송 삭제
     */
    @DeleteMapping("/{liveNo}")
    @Operation(summary = "라이브 방송 삭제", description = "라이브 방송을 삭제합니다. (진행 중이 아닌 경우만 가능)")
    public ResponseEntity<Void> deleteLiveRoom(
            @Parameter(description = "라이브 방송 번호") @PathVariable Long liveNo,
            @Parameter(description = "호스트 회원 번호") @RequestParam Integer hostMemNo) {
        
        liveRoomService.deleteLiveRoom(liveNo, hostMemNo);
        return ResponseEntity.noContent().build();
    }

    /**
     * 라이브 방송 접속자 수 조회
     */
    @GetMapping("/{liveNo}/usercount")
    @Operation(summary = "라이브 방송 접속자 수 조회", description = "현재 라이브 방송의 접속자 수를 조회합니다.")
    public ResponseEntity<Long> getCurrentUserCount(
            @Parameter(description = "라이브 방송 번호") @PathVariable Long liveNo) {
        
        Long userCount = liveRoomService.getCurrentUserCount(liveNo);
        return ResponseEntity.ok(userCount);
    }
}
