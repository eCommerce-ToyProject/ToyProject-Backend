package com.idrsys.toyprojectbackend.controller.memebr;

import com.idrsys.toyprojectbackend.config.jwt.JwtTokenProvider;
import com.idrsys.toyprojectbackend.dto.jwt.JwtToken;
import com.idrsys.toyprojectbackend.dto.member.MemberDto;
import com.idrsys.toyprojectbackend.dto.member.MemberSearchDto;
import com.idrsys.toyprojectbackend.dto.member.SignInDto;
import com.idrsys.toyprojectbackend.dto.member.SignUpDto;
import com.idrsys.toyprojectbackend.repository.memebr.MemberRepository;
import com.idrsys.toyprojectbackend.repository.memebr.MemberRepositoryCustom;
import com.idrsys.toyprojectbackend.service.MemberService;
import com.idrsys.toyprojectbackend.util.SecurityUtil;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public void signIn(@RequestBody SignInDto signInDto, HttpServletResponse response) {
        String id = signInDto.getId();
        String password = signInDto.getPassword();
        JwtToken jwtToken = memberService.signIn(id, password);
        log.info("request id = {}, password = {}", id, password);
        log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());

        Cookie accesstoken = new Cookie("accessToken", jwtToken.getAccessToken());
        accesstoken.setPath("/");
        accesstoken.setHttpOnly(true);
        accesstoken.setSecure(true);
        response.addCookie(accesstoken);

        Cookie refreshToken = new Cookie("refreshToken", jwtToken.getRefreshToken());
        refreshToken.setPath("/");
        refreshToken.setHttpOnly(true);
        refreshToken.setSecure(true);
        response.addCookie(refreshToken);

    }

//    @Operation(summary = "login check by access token - 엑세스 토큰으로 로그인 체크", description = "")
    @GetMapping("/loginCheck")
    public String loginCheck(HttpServletRequest request) {
        try {
            String id = SecurityUtil.getCurrentMemberId();
            return id;
        } catch (Exception e){
            return null;
        }

    }

//    @Operation(summary = "sign up - 회원가입", description = "")
    @PostMapping("/sign-up")
    public ResponseEntity<MemberDto> signUp(@RequestBody SignUpDto signUpDto) {
        MemberDto savedMemberDto = memberService.signUp(signUpDto);

        return ResponseEntity.ok(savedMemberDto);
    }
    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        Cookie accesstoken = new Cookie("accessToken", null);
        accesstoken.setPath("/");
        accesstoken.setMaxAge(0);
        response.addCookie(accesstoken);

        Cookie refreshToken = new Cookie("refreshToken",null);
        refreshToken.setPath("/");
        refreshToken.setMaxAge(0);
        response.addCookie(refreshToken);
    }

//    @Operation(summary = "comfirm by id - 아이디 중복체크", description = "")
    @GetMapping("/id/exists")
    public ResponseEntity<Boolean> checkIdDuplicate(@RequestParam String id){
        return ResponseEntity.ok(memberService.checkIdDuplicate(id));
    }

    @GetMapping("/orderingMyinfo")
    public List<MemberDto> myInfo(@RequestParam(name = "id") String id){
        return memberRepositoryCustom.memberOrdering(id);
    }

    @PostMapping("/reissuanceAccessToken")
    public void regenerateAccessToken(HttpServletRequest request,  HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        String refreshTokenCookie = null;
        if(cookies != null && cookies.length > 0 ) {
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals("refreshToken")) {
                    refreshTokenCookie = cookie.getValue();
                }
            }
        }
        JwtToken jwtToken = memberService.reissuanceAccessTokenWithRefreshToken(refreshTokenCookie);
        Cookie accesstoken = new Cookie("accessToken", jwtToken.getAccessToken());
        accesstoken.setPath("/");
        accesstoken.setHttpOnly(true);
        accesstoken.setSecure(true);
        response.addCookie(accesstoken);

        Cookie refreshToken = new Cookie("refreshToken", jwtToken.getRefreshToken());
        refreshToken.setPath("/");
        refreshToken.setHttpOnly(true);
        refreshToken.setSecure(true);
        response.addCookie(refreshToken);
    }

    @GetMapping("/memberList/Excel")
    public void memberListExcel(HttpServletResponse response){

    }

    @GetMapping("/memberList")
    public List<MemberSearchDto> memberList(){
        return memberRepositoryCustom.membersList();
    }

}
