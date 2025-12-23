package com.ecommerce.common.response;

import com.ecommerce.common.code.CommonResultCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 공통 API 응답 포맷
 * 모든 MSA 서비스에서 일관된 응답 구조를 제공
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private String code;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    
    /**
     * 성공 응답 (데이터 포함)
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
            CommonResultCode.SUCCESS.getCode(),
            CommonResultCode.SUCCESS.getMessage(),
            data,
            LocalDateTime.now()
        );
    }
    
    /**
     * 성공 응답 (데이터 없음)
     */
    public static <T> ApiResponse<T> success() {
        return success(null);
    }
    
    /**
     * 실패 응답 (CommonResultCode 사용)
     */
    public static <T> ApiResponse<T> failure(CommonResultCode resultCode) {
        return new ApiResponse<>(
            resultCode.getCode(),
            resultCode.getMessage(),
            null,
            LocalDateTime.now()
        );
    }
    
    /**
     * 실패 응답 (커스텀 메시지)
     */
    public static <T> ApiResponse<T> failure(CommonResultCode resultCode, String customMessage) {
        return new ApiResponse<>(
            resultCode.getCode(),
            customMessage,
            null,
            LocalDateTime.now()
        );
    }
    
    /**
     * 실패 응답 (커스텀 코드와 메시지)
     */
    public static <T> ApiResponse<T> failure(String code, String message) {
        return new ApiResponse<>(
            code,
            message,
            null,
            LocalDateTime.now()
        );
    }
    
    /**
     * 성공 여부 확인
     */
    public boolean isSuccess() {
        return CommonResultCode.SUCCESS.getCode().equals(this.code);
    }
}