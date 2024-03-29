package com.idrsys.toyprojectbackend.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Builder
@Getter
@RedisHash(value = "refresh", timeToLive = 60 * 60 * 24 * 14) // 2주
public class RefreshToken {

    @Id
    private String id;
    @Indexed
    private String refreshToken;
    
}
