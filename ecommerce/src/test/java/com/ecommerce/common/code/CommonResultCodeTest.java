package com.ecommerce.common.code;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CommonResultCode 테스트")
class CommonResultCodeTest {
    
    @Test
    @DisplayName("코드로 ResultCode 찾기 - 성공")
    void fromCode_Success() {
        // when
        CommonResultCode result = CommonResultCode.fromCode("0000");
        
        // then
        assertEquals(CommonResultCode.SUCCESS, result);
    }
    
    @Test
    @DisplayName("코드로 ResultCode 찾기 - 존재하지 않는 코드")
    void fromCode_NotFound() {
        // when & then
        assertThrows(IllegalArgumentException.class, 
            () -> CommonResultCode.fromCode("9999"));
    }
    
    @Test
    @DisplayName("ResultCode 속성 확인")
    void resultCodeProperties() {
        // given
        CommonResultCode success = CommonResultCode.SUCCESS;
        
        // then
        assertEquals("0000", success.getCode());
        assertEquals("성공", success.getMessage());
    }
}