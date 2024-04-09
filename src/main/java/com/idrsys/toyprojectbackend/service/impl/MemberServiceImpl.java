package com.idrsys.toyprojectbackend.service.impl;


import com.idrsys.toyprojectbackend.config.jwt.JwtTokenProvider;
import com.idrsys.toyprojectbackend.dto.jwt.JwtToken;
import com.idrsys.toyprojectbackend.dto.member.MemberDto;
import com.idrsys.toyprojectbackend.dto.member.SignUpDto;
import com.idrsys.toyprojectbackend.entity.Member;
import com.idrsys.toyprojectbackend.entity.RefreshToken;
import com.idrsys.toyprojectbackend.repository.RefreshTokenRedisRepository;
import com.idrsys.toyprojectbackend.repository.memebr.MemberRepository;
import com.idrsys.toyprojectbackend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final static int ACCESS_TOKEN_MAXAGE = 30*60;

    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public JwtToken signIn(String id, String password) {
        // 1. username + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(id, password);

        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        Member member = memberRepository.findById(id).orElseThrow(NullPointerException::new);
        String accessToken = jwtTokenProvider.generateAccessToken(member.getAccessTokenClaims(), authentication, ACCESS_TOKEN_MAXAGE);
        String refreshToken = jwtTokenProvider.generateRefreshToken(member);

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(saveRefreshToken(refreshToken, member))
                .build();
    }

    private String saveRefreshToken(String refreshTokenString, Member member){
        try {
            RefreshToken refreshToken = refreshTokenRedisRepository.findById(member.getId()).orElseThrow(NullPointerException::new);
            refreshToken.update(refreshTokenString);
            refreshTokenRedisRepository.save(refreshToken);
        }catch (NullPointerException e){
            return refreshTokenRedisRepository.save(RefreshToken.from(member.getId(), refreshTokenString)).getRefreshToken();
        }
        return refreshTokenString;
    }

    @Override
    public JwtToken reissuanceAccessTokenWithRefreshToken(String inputRefreshToken) {
        try{
            RefreshToken refreshToken = refreshTokenRedisRepository.findByRefreshToken(inputRefreshToken).orElseThrow(NullPointerException::new);
            Member member = memberRepository.findById(refreshToken.getId()).orElseThrow(NullPointerException::new);
            Collection<? extends GrantedAuthority> authorities = member.getRoles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    member.getId(),
                    null,
                    authorities
            );

            String newAccesstoken = jwtTokenProvider.generateAccessToken(member.getAccessTokenClaims(), authentication, ACCESS_TOKEN_MAXAGE);
            String refreshTokenRotation = jwtTokenProvider.generateRefreshToken(member);

            return JwtToken.builder()
                    .grantType("Bearer")
                    .accessToken(newAccesstoken)
                    .refreshToken(saveRefreshToken(refreshTokenRotation, member))
                    .build();
        }catch (NullPointerException e){
            if(CheckRefreshToken(inputRefreshToken)){
                deleteRefreshToken(getMemberByRefreshToken(inputRefreshToken).getId());
                throw new NullPointerException("Expired token");
            }
            throw new NullPointerException("Expired or invalid token");
        }
    }

    private Member getMemberByRefreshToken(String inputRefreshToken){
        try {
            return memberRepository.findByUsername(jwtTokenProvider.validationRefreshToken(inputRefreshToken)).orElseThrow(NullPointerException::new);
        }catch (NullPointerException e){
            throw new NullPointerException("Invalid refresh token");
        }

    }

    private boolean CheckRefreshToken(String inputRefreshToken){
        try {
            Member member = memberRepository.findByUsername(jwtTokenProvider.validationRefreshToken(inputRefreshToken)).orElseThrow(NullPointerException::new);
            return true;
        }catch (NullPointerException e){
            throw new NullPointerException("Invalid refresh token");
        }

    }

    @Override
    public void deleteRefreshToken(String id) {
        refreshTokenRedisRepository.deleteById(id);
    }

    private Authentication authenticateMember(Member member) {
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(
                new UsernamePasswordAuthenticationToken(member.getId(), member.getPassword().describeConstable()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }

    @Override
    @Transactional
    public MemberDto signUp(SignUpDto signUpDto) {
        // Password 암호화
        String encodedPassword = passwordEncoder.encode(signUpDto.getPassword());
        List<String> roles = new ArrayList<>();
        roles.add("USER");  // USER 권한 부여
        LocalDateTime now = LocalDateTime.now();
        Member member = memberRepository.save(signUpDto.toEntity(encodedPassword, roles, now));
        return MemberDto.toDto(member);
    }

    @Override
    public boolean checkIdDuplicate(String id) {
        return memberRepository.existsById(id);
    }

    @Override
    public Optional<Member> getMemberInfo(String id) {
        return memberRepository.findById(id);
    }
}
