package com.ecommerce.user.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 회원 서비스 전용 응답 코드
 * 로그인, 회원가입 등 회원 관련 세분화된 결과 코드
 */
@Getter
@RequiredArgsConstructor
public enum LoginResultCode {
    
    // 인증 관련
    LOGIN_SUCCESS("1000", "로그인 성공"),
    INVALID_CREDENTIALS("1001", "아이디 또는 비밀번호가 일치하지 않습니다"),
    USER_NOT_FOUND("1002", "존재하지 않는 계정입니다"),
    PASSWORD_MISMATCH("1003", "비밀번호가 일치하지 않습니다"),
    ACCOUNT_LOCKED("1004", "계정이 잠겨있습니다"),
    ACCOUNT_DISABLED("1005", "비활성화된 계정입니다"),
    
    // 회원가입 관련
    SIGNUP_SUCCESS("1010", "회원가입 성공"),
    EMAIL_ALREADY_EXISTS("1011", "이미 사용 중인 이메일입니다"),
    INVALID_EMAIL_FORMAT("1012", "올바르지 않은 이메일 형식입니다"),
    WEAK_PASSWORD("1013", "비밀번호가 보안 정책에 맞지 않습니다"),
    
    // 토큰 관련
    TOKEN_EXPIRED("1020", "토큰이 만료되었습니다"),
    INVALID_TOKEN("1021", "유효하지 않은 토큰입니다"),
    REFRESH_TOKEN_EXPIRED("1022", "리프레시 토큰이 만료되었습니다"),
    REFRESH_TOKEN_NOT_FOUND("1023", "리프레시 토큰을 찾을 수 없습니다"),
    
    // 권한 관련
    INSUFFICIENT_PRIVILEGES("1030", "권한이 부족합니다"),
    ROLE_NOT_FOUND("1031", "권한 정보를 찾을 수 없습니다");
    
    private final String code;
    private final String message;
    
    /**
     * 코드로 LoginResultCode 찾기
     */
    public static LoginResultCode fromCode(String code) {
        for (LoginResultCode resultCode : values()) {
            if (resultCode.code.equals(code)) {
                return resultCode;
            }
        }
        throw new IllegalArgumentException("Unknown login result code: " + code);
    }
}