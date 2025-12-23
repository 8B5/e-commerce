package com.ecommerce.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 공통 응답 코드 정의
 * MSA 전체에서 사용되는 범용 결과 코드
 */
@Getter
@RequiredArgsConstructor
public enum CommonResultCode {
    
    // 성공
    SUCCESS("0000", "성공"),
    
    // 클라이언트 오류 (4xxx)
    INVALID_PARAMETER("4001", "잘못된 파라미터입니다"),
    MISSING_REQUIRED_PARAMETER("4002", "필수 파라미터가 누락되었습니다"),
    INVALID_REQUEST_FORMAT("4003", "요청 형식이 올바르지 않습니다"),
    UNAUTHORIZED("4010", "인증이 필요합니다"),
    FORBIDDEN("4030", "접근 권한이 없습니다"),
    NOT_FOUND("4040", "요청한 리소스를 찾을 수 없습니다"),
    METHOD_NOT_ALLOWED("4050", "허용되지 않은 HTTP 메서드입니다"),
    CONFLICT("4090", "리소스 충돌이 발생했습니다"),
    
    // 서버 오류 (5xxx)
    INTERNAL_SERVER_ERROR("5000", "내부 서버 오류가 발생했습니다"),
    DATABASE_ERROR("5001", "데이터베이스 오류가 발생했습니다"),
    EXTERNAL_API_ERROR("5002", "외부 API 호출 중 오류가 발생했습니다"),
    SERVICE_UNAVAILABLE("5030", "서비스를 사용할 수 없습니다");
    
    private final String code;
    private final String message;
    
    /**
     * 코드로 ResultCode 찾기
     */
    public static CommonResultCode fromCode(String code) {
        for (CommonResultCode resultCode : values()) {
            if (resultCode.code.equals(code)) {
                return resultCode;
            }
        }
        throw new IllegalArgumentException("Unknown result code: " + code);
    }
}