package com.idrsys.toyprojectbackend.service;

import com.idrsys.toyprojectbackend.dto.chat.ChatMessageDto;
import com.idrsys.toyprojectbackend.dto.chat.ChatRequestDto;
import com.idrsys.toyprojectbackend.entity.Chat;
import com.idrsys.toyprojectbackend.entity.LiveRoom;
import com.idrsys.toyprojectbackend.entity.Member;
import com.idrsys.toyprojectbackend.repository.chat.ChatRepository;
import com.idrsys.toyprojectbackend.repository.chat.LiveRoomRepository;
import com.idrsys.toyprojectbackend.repository.memebr.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatService {

    private final ChatRepository chatRepository;
    private final LiveRoomRepository liveRoomRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String LIVE_ROOM_USERS_KEY = "live:room:users:";
    private static final String USER_ACTIVITY_KEY = "user:activity:";

    /**
     * 채팅 메시지 저장 및 브로드캐스트
     */
    public ChatMessageDto sendMessage(ChatRequestDto request) {
        // 라이브 방송 상태 확인
        LiveRoom liveRoom = liveRoomRepository.findByLiveNoAndStatus(
                request.getLiveNo(), LiveRoom.LiveStatus.LIVE)
                .orElseThrow(() -> new IllegalArgumentException("활성 상태의 라이브 방송을 찾을 수 없습니다."));

        // 회원 정보 조회
        Member member = memberRepository.findById(String.valueOf(request.getMemNo()))
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 채팅 메시지 저장
        Chat chat = Chat.builder()
                .liveNo(request.getLiveNo())
                .memNo(request.getMemNo())
                .message(request.getMessage())
                .build();

        Chat savedChat = chatRepository.save(chat);

        // DTO 변환
        ChatMessageDto messageDto = ChatMessageDto.builder()
                .chatNo(savedChat.getChatNo())
                .liveNo(savedChat.getLiveNo())
                .memNo(savedChat.getMemNo())
                .memName(member.getUsername())
                .message(savedChat.getMessage())
                .sendTime(savedChat.getSendTime())
                .type(request.getType())
                .build();

        // WebSocket으로 브로드캐스트
        messagingTemplate.convertAndSend("/topic/live/" + request.getLiveNo(), messageDto);

        // 사용자 활동 기록 (Redis)
        updateUserActivity(request.getMemNo(), request.getLiveNo());

        log.info("Chat message sent: liveNo={}, memNo={}, message={}", 
                request.getLiveNo(), request.getMemNo(), request.getMessage());

        return messageDto;
    }

    /**
     * 라이브 방송 입장
     */
    public ChatMessageDto joinLiveRoom(Long liveNo, Integer memNo) {
        // 라이브 방송 존재 확인
        LiveRoom liveRoom = liveRoomRepository.findByLiveNoAndStatus(liveNo, LiveRoom.LiveStatus.LIVE)
                .orElseThrow(() -> new IllegalArgumentException("활성 상태의 라이브 방송을 찾을 수 없습니다."));

        // 회원 정보 조회
        Member member = memberRepository.findById(String.valueOf(memNo))
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // Redis에 사용자 추가
        String roomKey = LIVE_ROOM_USERS_KEY + liveNo;
        redisTemplate.opsForSet().add(roomKey, memNo.toString());
        redisTemplate.expire(roomKey, 24, TimeUnit.HOURS);

        // 입장 메시지 생성
        ChatMessageDto joinMessage = ChatMessageDto.builder()
                .liveNo(liveNo)
                .memNo(memNo)
                .memName(member.getUsername())
                .message(member.getUsername() + "님이 입장하셨습니다.")
                .sendTime(LocalDateTime.now())
                .type(ChatMessageDto.MessageType.JOIN)
                .build();

        // 입장 메시지 브로드캐스트
        messagingTemplate.convertAndSend("/topic/live/" + liveNo, joinMessage);

        // 현재 접속자 수 업데이트 브로드캐스트
        long userCount = getCurrentUserCount(liveNo);
        messagingTemplate.convertAndSend("/topic/live/" + liveNo + "/usercount", userCount);

        log.info("User joined live room: liveNo={}, memNo={}, userName={}", 
                liveNo, memNo, member.getUsername());

        return joinMessage;
    }

    /**
     * 라이브 방송 퇴장
     */
    public ChatMessageDto leaveLiveRoom(Long liveNo, Integer memNo) {
        // 회원 정보 조회
        Optional<Member> memberOpt = memberRepository.findById(String.valueOf(memNo));

        // Redis에서 사용자 제거
        String roomKey = LIVE_ROOM_USERS_KEY + liveNo;
        redisTemplate.opsForSet().remove(roomKey, memNo.toString());

        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            // 퇴장 메시지 생성
            ChatMessageDto leaveMessage = ChatMessageDto.builder()
                    .liveNo(liveNo)
                    .memNo(memNo)
                    .memName(member.getUsername())
                    .message(member.getUsername() + "님이 퇴장하셨습니다.")
                    .sendTime(LocalDateTime.now())
                    .type(ChatMessageDto.MessageType.LEAVE)
                    .build();

            // 퇴장 메시지 브로드캐스트
            messagingTemplate.convertAndSend("/topic/live/" + liveNo, leaveMessage);

            log.info("User left live room: liveNo={}, memNo={}, userName={}", 
                    liveNo, memNo, member.getUsername());

            // 현재 접속자 수 업데이트 브로드캐스트
            long userCount = getCurrentUserCount(liveNo);
            messagingTemplate.convertAndSend("/topic/live/" + liveNo + "/usercount", userCount);

            return leaveMessage;
        }

        return null;
    }

    /**
     * 이전 채팅 메시지 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<ChatMessageDto> getChatHistory(Long liveNo, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Chat> chatPage = chatRepository.findByLiveNoOrderBySendTimeDesc(liveNo, pageable);

        return chatPage.map(this::convertToDto);
    }

    /**
     * 특정 시간 이후 채팅 메시지 조회
     */
    @Transactional(readOnly = true)
    public List<ChatMessageDto> getChatMessagesAfter(Long liveNo, LocalDateTime afterTime) {
        List<Chat> chats = chatRepository.findByLiveNoAndSendTimeAfterOrderBySendTimeAsc(liveNo, afterTime);
        return chats.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 현재 라이브 방송 접속자 수 조회
     */
    @Transactional(readOnly = true)
    public long getCurrentUserCount(Long liveNo) {
        String roomKey = LIVE_ROOM_USERS_KEY + liveNo;
        Set<Object> users = redisTemplate.opsForSet().members(roomKey);
        return users != null ? users.size() : 0;
    }

    /**
     * 현재 라이브 방송 접속자 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Integer> getCurrentUsers(Long liveNo) {
        String roomKey = LIVE_ROOM_USERS_KEY + liveNo;
        Set<Object> users = redisTemplate.opsForSet().members(roomKey);
        
        return users != null ? users.stream()
                .map(user -> Integer.valueOf(user.toString()))
                .collect(Collectors.toList()) : List.of();
    }

    /**
     * 사용자 활동 기록 업데이트
     */
    private void updateUserActivity(Integer memNo, Long liveNo) {
        String activityKey = USER_ACTIVITY_KEY + memNo + ":" + liveNo;
        redisTemplate.opsForValue().set(activityKey, LocalDateTime.now().toString(), 1, TimeUnit.HOURS);
    }

    /**
     * Chat 엔티티를 DTO로 변환
     */
    private ChatMessageDto convertToDto(Chat chat) {
        Optional<Member> memberOpt = memberRepository.findById(String.valueOf(chat.getMemNo()));
        
        return ChatMessageDto.builder()
                .chatNo(chat.getChatNo())
                .liveNo(chat.getLiveNo())
                .memNo(chat.getMemNo())
                .memName(memberOpt.map(Member::getUsername).orElse("알 수 없는 사용자"))
                .message(chat.getMessage())
                .sendTime(chat.getSendTime())
                .type(ChatMessageDto.MessageType.CHAT)
                .build();
    }
}
