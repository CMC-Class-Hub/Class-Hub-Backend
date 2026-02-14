package com.cmc.classhub.global.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.cmc.classhub.global.error.exception.InvalidTokenException;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String token = parseBearerToken(request);

    // 1. 토큰이 없는 경우: 다음 필터로 진행 (인증 정보 없이)
    if (token == null) {
      filterChain.doFilter(request, response);
      return;
    }

    // 2. 토큰이 유효한 경우: 인증 정보 설정 및 다음 필터로 진행
    if (jwtProvider.validateToken(token)) {
      Long userId = jwtProvider.parseUserId(token);
      String role = jwtProvider.parseRole(token);

      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          userId,
          null,
          Collections.singleton(new SimpleGrantedAuthority(role)));

      SecurityContextHolder.getContext().setAuthentication(authentication);
      filterChain.doFilter(request, response);
    } else {
      // 3. 토큰이 있지만 유효하지 않은 경우 (만료, 변조 등): 예외 발생시킴 -> AuthenticationEntryPoint ->
      // GlobalExceptionHandler
      throw new InvalidTokenException("유효하지 않은 토큰입니다.");
    }
  }

  private String parseBearerToken(HttpServletRequest request) {
    if (request.getCookies() == null) {
      return null;
    }
    for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
      if (cookie.getName().equals("accessToken")) {
        return cookie.getValue();
      }
    }
    return null;
  }
}
