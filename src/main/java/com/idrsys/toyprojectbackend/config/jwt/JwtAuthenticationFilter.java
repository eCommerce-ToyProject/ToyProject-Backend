package com.idrsys.toyprojectbackend.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idrsys.toyprojectbackend.exception.JwtAuthenticationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 1. Request Header에서 JWT 토큰 추출
        String token = resolveToken((HttpServletRequest) request);

        // 2. validateToken으로 토큰 유효성 검사
        if (token != null) {
            try {
                if(jwtTokenProvider.validateToken(token)){
                    Map<String,Object> claims = jwtTokenProvider.getClaims(token);
                    // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info(String.valueOf(authentication));
                }
            } catch (ExpiredJwtException e) {
                log.error(e.getMessage());
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Access token has expired");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private void sendErrorResponse(ServletResponse response, HttpStatus status, String message) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(status.value());
        httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = objectMapper.writeValueAsString(Map.of("error", message));

        httpResponse.getWriter().write(responseBody);
    }

    // Request Header에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        Cookie[] cookies = request.getCookies();
        String accessToken = "";
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            accessToken = bearerToken.substring(7);
            return accessToken;
        }
//        if(cookies != null && cookies.length > 0 ) {
//            for (Cookie cookie : cookies) {
//                if(cookie.getName().equals("accessToken")) {
//                    accessToken = cookie.getValue();
//                }
//            }
//            if(Objects.equals(accessToken, "undefined")){
//                return null;
//            }
//            return accessToken;
//        }
        return null;
    }

}
