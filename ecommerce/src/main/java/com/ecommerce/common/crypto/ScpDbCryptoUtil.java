package com.ecommerce.common.crypto;

/**
 * 데이터베이스 암호화 인터페이스
 * DIP(의존성 역전 원칙)를 준수하여 구현체를 교체 가능하도록 설계
 */
public interface ScpDbCryptoUtil {
    
    /**
     * 평문을 암호화
     * @param plainText 암호화할 평문
     * @return 암호화된 문자열
     */
    String encrypt(String plainText);
    
    /**
     * 암호문을 복호화
     * @param encryptedText 복호화할 암호문
     * @return 복호화된 평문
     */
    String decrypt(String encryptedText);
    
    /**
     * 비밀번호 해싱 (단방향)
     * @param password 해싱할 비밀번호
     * @return 해싱된 비밀번호
     */
    String hashPassword(String password);
    
    /**
     * 비밀번호 검증
     * @param rawPassword 평문 비밀번호
     * @param hashedPassword 해싱된 비밀번호
     * @return 일치 여부
     */
    boolean verifyPassword(String rawPassword, String hashedPassword);
}