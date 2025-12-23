package com.ecommerce.common.response;

import com.ecommerce.common.code.CommonResultCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ApiResponse 테스트")
class ApiResponseTest {
    
    @Test
    @DisplayName("성공 응답 - 데이터 포함")
    void success_WithData() {
        // given
        String testData = "테스트 데이터";
        
        // when
        ApiResponse<String> response = ApiResponse.success(testData);
        
        // then
        assertEquals(CommonResultCode.SUCCESS.getCode(), response.getCode());
        assertEquals(CommonResultCode.SUCCESS.getMessage(), response.getMessage());
        assertEquals(testData, response.getData());
        assertNotNull(response.getTimestamp());
        assertTrue(response.isSuccess());
    }
    
    @Test
    @DisplayName("성공 응답 - 데이터 없음")
    void success_WithoutData() {
        // when
        ApiResponse<Void> response = ApiResponse.success();
        
        // then
        assertEquals(CommonResultCode.SUCCESS.getCode(), response.getCode());
        assertEquals(CommonResultCode.SUCCESS.getMessage(), response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
        assertTrue(response.isSuccess());
    }
    
    @Test
    @DisplayName("실패 응답 - CommonResultCode 사용")
    void failure_WithResultCode() {
        // when
        ApiResponse<Void> response = ApiResponse.failure(CommonResultCode.INVALID_PARAMETER);
        
        // then
        assertEquals(CommonResultCode.INVALID_PARAMETER.getCode(), response.getCode());
        assertEquals(CommonResultCode.INVALID_PARAMETER.getMessage(), response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
        assertFalse(response.isSuccess());
    }
    
    @Test
    @DisplayName("실패 응답 - 커스텀 메시지")
    void failure_WithCustomMessage() {
        // given
        String customMessage = "커스텀 오류 메시지";
        
        // when
        ApiResponse<Void> response = ApiResponse.failure(CommonResultCode.INVALID_PARAMETER, customMessage);
        
        // then
        assertEquals(CommonResultCode.INVALID_PARAMETER.getCode(), response.getCode());
        assertEquals(customMessage, response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
        assertFalse(response.isSuccess());
    }
    
    @Test
    @DisplayName("실패 응답 - 커스텀 코드와 메시지")
    void failure_WithCustomCodeAndMessage() {
        // given
        String customCode = "9999";
        String customMessage = "커스텀 오류";
        
        // when
        ApiResponse<Void> response = ApiResponse.failure(customCode, customMessage);
        
        // then
        assertEquals(customCode, response.getCode());
        assertEquals(customMessage, response.getMessage());
        assertNull(response.getData());
        assertNotNull(response.getTimestamp());
        assertFalse(response.isSuccess());
    }
}