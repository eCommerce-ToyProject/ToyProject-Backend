package com.idrsys.toyprojectbackend.repository.chat;

import com.idrsys.toyprojectbackend.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    
    /**
     * 특정 라이브 방송의 채팅 메시지를 시간순으로 조회
     */
    List<Chat> findByLiveNoOrderBySendTimeAsc(Long liveNo);
    
    /**
     * 특정 라이브 방송의 채팅 메시지를 페이징으로 조회 (최신순)
     */
    Page<Chat> findByLiveNoOrderBySendTimeDesc(Long liveNo, Pageable pageable);
    
    /**
     * 특정 시간 이후의 채팅 메시지 조회
     */
    List<Chat> findByLiveNoAndSendTimeAfterOrderBySendTimeAsc(Long liveNo, LocalDateTime afterTime);
    
    /**
     * 특정 라이브 방송의 채팅 개수 조회
     */
    long countByLiveNo(Long liveNo);
    
    /**
     * 특정 회원의 특정 라이브 방송 채팅 조회
     */
    List<Chat> findByLiveNoAndMemNoOrderBySendTimeDesc(Long liveNo, Integer memNo);
    
    /**
     * 최근 채팅 메시지 조회 (JOIN으로 회원 정보 포함)
     */
    @Query("SELECT c FROM Chat c WHERE c.liveNo = :liveNo ORDER BY c.sendTime DESC")
    Page<Chat> findRecentChatsByLiveNo(@Param("liveNo") Long liveNo, Pageable pageable);
}
