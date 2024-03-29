package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {


}
