package com.idrsys.toyprojectbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Chat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_no")
    private Long chatNo;
    
    @Column(name = "live_no", nullable = false)
    private Long liveNo;
    
    @Column(name = "mem_no", nullable = false)
    private Integer memNo;
    
    @Column(name = "message", nullable = false, length = 1000)
    private String message;
    
    @CreatedDate
    @Column(name = "send_time")
    private LocalDateTime sendTime;
    
    // 연관관계 매핑 (필요시)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "live_no", insertable = false, updatable = false)
    private LiveRoom liveRoom;
}
