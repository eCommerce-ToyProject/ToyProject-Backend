package com.idrsys.toyprojectbackend.repository.chat;

import com.idrsys.toyprojectbackend.entity.LiveRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LiveRoomRepository extends JpaRepository<LiveRoom, Long> {
    
    /**
     * 현재 라이브 중인 방송 조회
     */
    List<LiveRoom> findByStatusOrderByStartTimeDesc(LiveRoom.LiveStatus status);
    
    /**
     * 현재 라이브 중인 방송 조회 (페이징)
     */
    Page<LiveRoom> findByStatusOrderByStartTimeDesc(LiveRoom.LiveStatus status, Pageable pageable);
    
    /**
     * 특정 호스트의 라이브 방송 조회
     */
    List<LiveRoom> findByHostMemNoOrderByStartTimeDesc(Integer hostMemNo);
    
    /**
     * 특정 호스트의 특정 상태 라이브 방송 조회 (추가됨)
     */
    List<LiveRoom> findByHostMemNoAndStatus(Integer hostMemNo, LiveRoom.LiveStatus status);
    
    /**
     * 특정 카테고리의 라이브 방송 조회
     */
    List<LiveRoom> findByCatCdAndStatusOrderByStartTimeDesc(Integer catCd, LiveRoom.LiveStatus status);
    
    /**
     * 라이브 방송 존재 여부 및 상태 확인
     */
    @Query("SELECT lr FROM LiveRoom lr WHERE lr.liveNo = :liveNo AND lr.status = :status")
    Optional<LiveRoom> findByLiveNoAndStatus(@Param("liveNo") Long liveNo, @Param("status") LiveRoom.LiveStatus status);
    
    /**
     * 현재 활성 라이브 방송 개수
     */
    long countByStatus(LiveRoom.LiveStatus status);
}
