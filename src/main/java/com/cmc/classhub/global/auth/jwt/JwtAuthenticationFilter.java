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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;

  @Override
  protected boolean shouldNotFilter(jakarta.servlet.http.HttpServletRequest request) {
    String origin = request.getHeader("Origin");
    String path = request.getRequestURI();

    if (origin != null && (origin.equals("https://classhub-link.vercel.app") ||
        origin.equals("http://localhost:3001"))) {
      return true;
    }

    return path.startsWith("/api/auth/login") ||
        path.startsWith("/api/auth/signup") ||
        path.startsWith("/api/reservations") ||
        path.startsWith("/api/students") ||
        path.equals("/health") ||
        path.startsWith("/h2-console");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      String token = parseBearerToken(request);

      if (token != null) {
        Long userId = jwtProvider.parseUserId(token);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userId,
            null,
            Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")) // 임시 Role
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception e) {
      log.error("Cannot set user authentication: {}", e.getMessage());
    }

    filterChain.doFilter(request, response);
  }

  private String parseBearerToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
