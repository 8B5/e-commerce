package com.ecommerce.common.crypto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DefaultScpDbCryptoUtil 테스트")
class DefaultScpDbCryptoUtilTest {
    
    private DefaultScpDbCryptoUtil cryptoUtil;
    
    @BeforeEach
    void setUp() {
        cryptoUtil = new DefaultScpDbCryptoUtil("");
    }
    
    @Test
    @DisplayName("암호화/복호화 - 정상 케이스")
    void encryptDecrypt_Success() {
        // given
        String plainText = "테스트 암호화 데이터";
        
        // when
        String encrypted = cryptoUtil.encrypt(plainText);
        String decrypted = cryptoUtil.decrypt(encrypted);
        
        // then
        assertNotNull(encrypted);
        assertNotEquals(plainText, encrypted);
        assertEquals(plainText, decrypted);
    }
    
    @Test
    @DisplayName("암호화 - 같은 평문도 다른 암호문 생성 (IV 사용)")
    void encrypt_DifferentCiphertext() {
        // given
        String plainText = "동일한 평문";
        
        // when
        String encrypted1 = cryptoUtil.encrypt(plainText);
        String encrypted2 = cryptoUtil.encrypt(plainText);
        
        // then
        assertNotEquals(encrypted1, encrypted2);
        assertEquals(plainText, cryptoUtil.decrypt(encrypted1));
        assertEquals(plainText, cryptoUtil.decrypt(encrypted2));
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("암호화 - null/빈 값")
    void encrypt_NullOrEmpty(String input) {
        // when
        String result = cryptoUtil.encrypt(input);
        
        // then
        assertEquals(input, result);
    }
    
    @Test
    @DisplayName("비밀번호 해싱/검증 - 정상 케이스")
    void hashVerifyPassword_Success() {
        // given
        String password = "mySecretPassword123!";
        
        // when
        String hashedPassword = cryptoUtil.hashPassword(password);
        boolean isValid = cryptoUtil.verifyPassword(password, hashedPassword);
        boolean isInvalid = cryptoUtil.verifyPassword("wrongPassword", hashedPassword);
        
        // then
        assertNotNull(hashedPassword);
        assertNotEquals(password, hashedPassword);
        assertTrue(isValid);
        assertFalse(isInvalid);
    }
    
    @Test
    @DisplayName("비밀번호 해싱 - 같은 비밀번호도 다른 해시 생성 (Salt 사용)")
    void hashPassword_DifferentHash() {
        // given
        String password = "samePassword";
        
        // when
        String hash1 = cryptoUtil.hashPassword(password);
        String hash2 = cryptoUtil.hashPassword(password);
        
        // then
        assertNotEquals(hash1, hash2);
        assertTrue(cryptoUtil.verifyPassword(password, hash1));
        assertTrue(cryptoUtil.verifyPassword(password, hash2));
    }
    
    @Test
    @DisplayName("비밀번호 해싱 - null/빈 값 예외")
    void hashPassword_NullOrEmpty() {
        // when & then
        assertThrows(IllegalArgumentException.class, 
            () -> cryptoUtil.hashPassword(null));
        assertThrows(IllegalArgumentException.class, 
            () -> cryptoUtil.hashPassword(""));
        assertThrows(IllegalArgumentException.class, 
            () -> cryptoUtil.hashPassword("   "));
    }
    
    @Test
    @DisplayName("비밀번호 검증 - null 처리")
    void verifyPassword_NullHandling() {
        // given
        String validHash = cryptoUtil.hashPassword("password");
        
        // when & then
        assertFalse(cryptoUtil.verifyPassword(null, validHash));
        assertFalse(cryptoUtil.verifyPassword("password", null));
        assertFalse(cryptoUtil.verifyPassword(null, null));
    }
}