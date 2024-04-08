package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    void deleteByRefreshToken(String refreshToken);
}
