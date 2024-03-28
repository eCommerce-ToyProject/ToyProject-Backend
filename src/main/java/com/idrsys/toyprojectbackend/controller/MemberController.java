package com.idrsys.toyprojectbackend.controller;

import com.idrsys.toyprojectbackend.config.jwt.JwtTokenProvider;
import com.idrsys.toyprojectbackend.dto.*;
import com.idrsys.toyprojectbackend.entity.Member;
import com.idrsys.toyprojectbackend.repository.MemberRepository;
import com.idrsys.toyprojectbackend.repository.MemberRepositoryCustom;
import com.idrsys.toyprojectbackend.service.MemberService;
import com.idrsys.toyprojectbackend.util.SecurityUtil;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//@Tag(name = "회원", description = "회원 관련 api 입니다.")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private JwtTokenProvider jwtTokenProvider;

    private final MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberRepositoryCustom memberRepositoryCustom;

//    @Operation(summary = "sign in - 로그인", description = "")
    @PostMapping("/sign-in")
    public JwtToken signIn(@RequestBody SignInDto signInDto) {
        String id = signInDto.getId();
        String password = signInDto.getPassword();
        JwtToken jwtToken = memberService.signIn(id, password);
        log.info("request id = {}, password = {}", id, password);
        log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        return jwtToken;
    }

//    @Operation(summary = "login check by access token - 엑세스 토큰으로 로그인 체크", description = "")
    @PostMapping("/loginCheck")
//    public List<String> loginCheck() {
    public String loginCheck(HttpServletRequest request) {
        String id = SecurityUtil.getCurrentMemberId();
        return id;
    }

//    @Operation(summary = "sign up - 회원가입", description = "")
    @PostMapping("/sign-up")
    public ResponseEntity<MemberDto> signUp(@RequestBody SignUpDto signUpDto) {
        MemberDto savedMemberDto = memberService.signUp(signUpDto);
        return ResponseEntity.ok(savedMemberDto);
    }

//    @Operation(summary = "comfirm by id - 아이디 중복체크", description = "")
    @GetMapping("/id/exists")
    public ResponseEntity<Boolean> checkIdDuplicate(@RequestParam String id){
        return ResponseEntity.ok(memberService.checkIdDuplicate(id));
    }

    @GetMapping("/orderingMyinfo")
    public List<MemberDto> myInfo(@RequestParam String id){
        return memberRepositoryCustom.memberOrdering(id);
    }

}
