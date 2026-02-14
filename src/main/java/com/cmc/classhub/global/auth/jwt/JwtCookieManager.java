package com.cmc.classhub.global.auth.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtCookieManager {

  @Value("${security.jwt.cookie.secure}")
  private boolean isSecure;

  @Value("${security.jwt.access-exp-seconds}")
  private long accessExp;

  @Value("${security.jwt.refresh-exp-seconds}")
  private long refreshExp;

  public void addAccessTokenCookie(HttpServletResponse response, String token) {
    response.addCookie(createCookie("accessToken", token, "/", accessExp));
  }

  public void addRefreshTokenCookie(HttpServletResponse response, String token) {
    response.addCookie(createCookie("refreshToken", token, "/api/auth", refreshExp));
  }

  public void clearTokenCookies(HttpServletResponse response) {
    response.addCookie(createCookie("accessToken", null, "/", 0));
    response.addCookie(createCookie("refreshToken", null, "/api/auth", 0));
  }

  private Cookie createCookie(String name, String value, String path, long maxAge) {
    Cookie cookie = new Cookie(name, value);
    cookie.setHttpOnly(true);
    cookie.setSecure(isSecure);
    cookie.setPath(path);
    cookie.setMaxAge((int) maxAge);
    return cookie;
  }
}
