package com.idrsys.toyprojectbackend.controller;

import com.idrsys.toyprojectbackend.dto.JwtToken;
import com.idrsys.toyprojectbackend.dto.MemberDto;
import com.idrsys.toyprojectbackend.dto.SignInDto;
import com.idrsys.toyprojectbackend.dto.SignUpDto;
import com.idrsys.toyprojectbackend.service.MemberService;
import com.idrsys.toyprojectbackend.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원", description = "회원 관련 api 입니다.")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "sign in - 로그인", description = "")
    @PostMapping("/sign-in")
    public JwtToken signIn(@RequestBody SignInDto signInDto) {
        String id = signInDto.getId();
        String password = signInDto.getPassword();
        JwtToken jwtToken = memberService.signIn(id, password);
        log.info("request id = {}, password = {}", id, password);
        log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        return jwtToken;
    }

    @Operation(summary = "access token 테스트", description = "")
    @PostMapping("/loginCheck")
    public String loginCheck() {
        return SecurityUtil.getCurrentMemberId();
    }

    @Operation(summary = "sign up - 회원가입", description = "")
    @PostMapping("/sign-up")
    public ResponseEntity<MemberDto> signUp(@RequestBody SignUpDto signUpDto) {
        MemberDto savedMemberDto = memberService.signUp(signUpDto);
        return ResponseEntity.ok(savedMemberDto);
    }

    @Operation(summary = "comfirm by id 아이디 중복체크", description = "")
    @GetMapping("/id/exists")
    public ResponseEntity<Boolean> checkIdDuplicate(@RequestParam String id){
        return ResponseEntity.ok(memberService.checkIdDuplicate(id));
    }

}
