package com.ecommerce.common.crypto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * ScpDbCryptoUtil의 기본 구현체
 * AES-256-GCM을 사용한 양방향 암호화와 SHA-256 기반 비밀번호 해싱 제공
 */
@Slf4j
@Component
public class DefaultScpDbCryptoUtil implements ScpDbCryptoUtil {
    
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    
    private final SecretKey secretKey;
    private final SecureRandom secureRandom;
    
    public DefaultScpDbCryptoUtil(@Value("${app.crypto.secret-key:}") String secretKeyString) {
        this.secureRandom = new SecureRandom();
        this.secretKey = initializeSecretKey(secretKeyString);
    }
    
    private SecretKey initializeSecretKey(String secretKeyString) {
        try {
            if (secretKeyString != null && !secretKeyString.isBlank()) {
                // 설정된 키가 있으면 사용
                byte[] keyBytes = Base64.getDecoder().decode(secretKeyString);
                return new SecretKeySpec(keyBytes, AES_ALGORITHM);
            } else {
                // 개발 환경용 기본 키 생성 (운영에서는 반드시 설정 필요)
                log.warn("암호화 키가 설정되지 않았습니다. 개발용 기본 키를 사용합니다.");
                KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
                keyGenerator.init(256);
                return keyGenerator.generateKey();
            }
        } catch (Exception e) {
            throw new RuntimeException("암호화 키 초기화 실패", e);
        }
    }
    
    @Override
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isBlank()) {
            return plainText;
        }
        
        try {
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            
            // IV 생성
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);
            
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
            
            byte[] encryptedData = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            
            // IV + 암호화된 데이터를 결합하여 Base64 인코딩
            byte[] encryptedWithIv = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
            System.arraycopy(encryptedData, 0, encryptedWithIv, iv.length, encryptedData.length);
            
            return Base64.getEncoder().encodeToString(encryptedWithIv);
            
        } catch (Exception e) {
            log.error("암호화 실패", e);
            throw new RuntimeException("암호화 처리 중 오류가 발생했습니다", e);
        }
    }
    
    @Override
    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isBlank()) {
            return encryptedText;
        }
        
        try {
            byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedText);
            
            // IV와 암호화된 데이터 분리
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encryptedData = new byte[encryptedWithIv.length - GCM_IV_LENGTH];
            
            System.arraycopy(encryptedWithIv, 0, iv, 0, iv.length);
            System.arraycopy(encryptedWithIv, iv.length, encryptedData, 0, encryptedData.length);
            
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);
            
            byte[] decryptedData = cipher.doFinal(encryptedData);
            return new String(decryptedData, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            log.error("복호화 실패", e);
            throw new RuntimeException("복호화 처리 중 오류가 발생했습니다", e);
        }
    }
    
    @Override
    public String hashPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 null이거나 빈 값일 수 없습니다");
        }
        
        try {
            // Salt 생성
            byte[] salt = new byte[16];
            secureRandom.nextBytes(salt);
            
            // 비밀번호 + Salt 해싱
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(salt);
            byte[] hashedPassword = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Salt + Hash를 결합하여 Base64 인코딩
            byte[] saltAndHash = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, saltAndHash, 0, salt.length);
            System.arraycopy(hashedPassword, 0, saltAndHash, salt.length, hashedPassword.length);
            
            return Base64.getEncoder().encodeToString(saltAndHash);
            
        } catch (Exception e) {
            log.error("비밀번호 해싱 실패", e);
            throw new RuntimeException("비밀번호 해싱 처리 중 오류가 발생했습니다", e);
        }
    }
    
    @Override
    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) {
            return false;
        }
        
        try {
            byte[] saltAndHash = Base64.getDecoder().decode(hashedPassword);
            
            // Salt와 Hash 분리
            byte[] salt = new byte[16];
            byte[] hash = new byte[saltAndHash.length - 16];
            
            System.arraycopy(saltAndHash, 0, salt, 0, salt.length);
            System.arraycopy(saltAndHash, salt.length, hash, 0, hash.length);
            
            // 입력된 비밀번호를 같은 Salt로 해싱
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(salt);
            byte[] inputHash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            
            // 해시 비교
            return MessageDigest.isEqual(hash, inputHash);
            
        } catch (Exception e) {
            log.error("비밀번호 검증 실패", e);
            return false;
        }
    }
}