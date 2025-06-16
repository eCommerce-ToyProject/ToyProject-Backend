package com.idrsys.toyprojectbackend.service;

import com.idrsys.toyprojectbackend.dto.jwt.JwtToken;
import com.idrsys.toyprojectbackend.dto.member.MemberDto;
import com.idrsys.toyprojectbackend.dto.member.SignUpDto;
import com.idrsys.toyprojectbackend.entity.Member;
import jakarta.servlet.http.Cookie;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface MemberService {

    @Transactional
    JwtToken signIn(String id, String password);

    JwtToken reissuanceAccessTokenWithRefreshToken(String inputRefreshToken);

    void deleteRefreshToken(String refreshToken);

    @Transactional
    MemberDto signUp(SignUpDto signUpDto);

    boolean checkIdDuplicate(String id);

    Optional<Member> getMemberInfo(String id);

    List<Member> getMembers();
    
    /**
     * 회원 번호로 회원 조회
     */
    Optional<Member> findById(Integer memNo);
    
    /**
     * ID로 회원 조회 (문자열)
     */
    Optional<Member> findById(String id);
}
