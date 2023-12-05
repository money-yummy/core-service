package com.moneyyummy.coreservice.error;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ErrorMessage {
    EMPTY("서버에러", 500),
    COMMON_TOKEN("올바르지 않거나 잘못된 토큰입니다.", 400),
    MALFORMED_TOKEN("토큰의 형식이 올바르지 않습니다.", 400),
    UNSUPPORTED_TOKEN("토큰에 지원되지 않는 내용이 있습니다.", 400),
    SIGNATURE_TOKEN("토큰의 서명이 올바르지 않습니다.", 400),
    EXPIRED_TOKEN("토큰이 만료되었습니다.", 401),
    JSON_PROCESSING("JSON 변환 실패", 400);

    private final String message;
    private final int code;

    ErrorMessage(String message, int httpCode) {
        this.message = message;
        this.code = httpCode;
    }

    public static ErrorMessage ofMessage(String message) {
        return Arrays.stream(ErrorMessage.values())
                .filter(e -> e.getMessage().equals(message))
                .findFirst()
                .orElse(ErrorMessage.EMPTY);
    }

    public static ErrorMessage ofException(Exception e) {
        return switch (e.getClass().getSimpleName()) {
            case "ExpiredJwtException" -> EXPIRED_TOKEN;
            case "MalformedJwtException" -> MALFORMED_TOKEN;
            case "SignatureException" -> SIGNATURE_TOKEN;
            case "UnsupportedJwtException" -> UNSUPPORTED_TOKEN;
            default -> COMMON_TOKEN;
        };
    }
}
