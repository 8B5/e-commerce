package com.ecommerce.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MaskingUtils 테스트")
class MaskingUtilsTest {
    
    @ParameterizedTest
    @DisplayName("이름 마스킹 - 정상 케이스")
    @CsvSource({
        "홍, 홍",
        "홍길, 홍*",
        "홍길동, 홍*동",
        "김철수, 김*수",
        "이영희, 이*희"
    })
    void maskName_Success(String input, String expected) {
        // when
        String result = MaskingUtils.maskName(input);
        
        // then
        assertEquals(expected, result);
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("이름 마스킹 - null/빈 값")
    void maskName_NullOrEmpty(String input) {
        // when
        String result = MaskingUtils.maskName(input);
        
        // then
        assertEquals(input, result);
    }
    
    @ParameterizedTest
    @DisplayName("이메일 마스킹 - 정상 케이스")
    @CsvSource({
        "abc@example.com, abc@example.com",
        "test@example.com, tes*@example.com",
        "user1234@example.com, use*****@example.com",
        "a@example.com, a*@example.com"
    })
    void maskEmail_Success(String input, String expected) {
        // when
        String result = MaskingUtils.maskEmail(input);
        
        // then
        assertEquals(expected, result);
    }
    
    @Test
    @DisplayName("이메일 마스킹 - 잘못된 형식")
    void maskEmail_InvalidFormat() {
        // given
        String invalidEmail = "notanemail";
        
        // when
        String result = MaskingUtils.maskEmail(invalidEmail);
        
        // then
        assertEquals(invalidEmail, result);
    }
    
    @ParameterizedTest
    @DisplayName("전화번호 마스킹 - 정상 케이스")
    @CsvSource({
        "010-1234-5678, 010-****-5678",
        "01012345678, 010-****-5678",
        "02-123-4567, 02-***-4567",
        "0212345678, 02-***-4567"
    })
    void maskPhoneNumber_Success(String input, String expected) {
        // when
        String result = MaskingUtils.maskPhoneNumber(input);
        
        // then
        assertEquals(expected, result);
    }
    
    @Test
    @DisplayName("커스텀 마스킹 - 정상 케이스")
    void maskCustom_Success() {
        // given
        String input = "1234567890";
        
        // when
        String result = MaskingUtils.maskCustom(input, 3, 7);
        
        // then
        assertEquals("123****890", result);
    }
    
    @Test
    @DisplayName("커스텀 마스킹 - 잘못된 범위")
    void maskCustom_InvalidRange() {
        // given
        String input = "1234567890";
        
        // when & then
        assertThrows(IllegalArgumentException.class, 
            () -> MaskingUtils.maskCustom(input, 7, 3));
    }
}