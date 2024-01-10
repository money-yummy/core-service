package com.moneyyummy.coreservice.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {

    public static final String SET_COOKIE = "Set-Cookie";

    public ResponseCookie ofToken(String accessToken) {
        return ResponseCookie.from(JwtTokenUtils.AUTHORIZATION, accessToken)
                .path("/")
                .maxAge(3600000)
                .build();
    }

    public void addCookie(HttpServletResponse response, HttpCookie cookie) {
        response.setHeader(SET_COOKIE, cookie.toString());
    }

    public void deleteCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public void deleteCookie(ServerHttpResponse response, String cookieName) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .maxAge(0)
                .path("/")
                .build();
        response.addCookie(cookie);
    }
}
