package com.moneyyummy.coreservice.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {

    public ResponseCookie ofToken(String accessToken) {
        return ResponseCookie.from(JwtTokenUtils.AUTHORIZATION, accessToken)
                .path("/")
                .maxAge(3600000)
                .build();
    }

    public void addCookie(HttpServletResponse response, HttpCookie cookie) {
        response.setHeader("Set-Cookie", cookie.toString());
    }
}
