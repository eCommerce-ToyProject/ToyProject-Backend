package com.idrsys.toyprojectbackend.service;

import com.idrsys.toyprojectbackend.dto.jwt.JwtToken;
import com.idrsys.toyprojectbackend.dto.member.MemberDto;
import com.idrsys.toyprojectbackend.dto.member.SignUpDto;
import com.idrsys.toyprojectbackend.entity.Member;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


public interface MemberService {


    @Transactional
    JwtToken signIn(String id, String password);

    JwtToken reissuanceAccessTokenWithRefreshToken(String inputRefreshToken);

    @Transactional
    MemberDto signUp(SignUpDto signUpDto);

    boolean checkIdDuplicate(String id);

    Optional<Member> getMemberInfo(String id);
}
