package com.idrsys.toyprojectbackend.repository.memebr;

import com.idrsys.toyprojectbackend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByUsername(String username);

    Optional<Member> findById(String id);

    boolean existsById(String id);
    
    /**
     * 회원 번호로 조회 (no 필드로 조회)
     */
    Optional<Member> findByNo(Long no);
    
    /**
     * 삭제되지 않은 회원만 조회 (ID로)
     */
    Optional<Member> findByIdAndMemDeletedFalse(String id);
    
    /**
     * 삭제되지 않은 회원만 조회 (회원번호로)
     */
    Optional<Member> findByNoAndMemDeletedFalse(Long no);
}

