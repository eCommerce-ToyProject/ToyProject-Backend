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

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static RefreshToken from(String id, String refreshToken) {
        return RefreshToken.builder()
                .id(id)
                .refreshToken(refreshToken)
                .build();
    }

    public void update(String sb) {
        this.setRefreshToken(sb);
    }
    
}
