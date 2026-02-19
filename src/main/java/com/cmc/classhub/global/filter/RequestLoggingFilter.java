package com.cmc.classhub.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("[REQUEST] {} {} | From: {}",
                request.getMethod(),
                request.getRequestURI(),
                getRequestSource(request));

        filterChain.doFilter(request, response);
    }

    private String getRequestSource(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null && userAgent.contains("ELB-HealthChecker")) {
            return "ELB";
        }

        String origin = request.getHeader("Origin");
        if (origin != null) {
            return origin;
        }

        String referer = request.getHeader("Referer");
        if (referer != null) {
            return referer;
        }

        return request.getRemoteAddr();
    }
}
