package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByUsername(String username);

    Optional<Member> findById(String id);

    boolean existsById(String id);



}
