package com.ecommerce.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 개인정보 마스킹 유틸리티
 * 이름, 이메일, 전화번호 등의 민감 정보를 마스킹 처리
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MaskingUtils {
    
    private static final String MASK_CHAR = "*";
    
    /**
     * 이름 마스킹
     * 2자: 첫 글자만 표시 (예: 홍* )
     * 3자 이상: 첫/마지막 글자만 표시 (예: 홍*동)
     */
    public static String maskName(String name) {
        if (name == null || name.isBlank()) {
            return name;
        }
        
        int length = name.length();
        
        return switch (length) {
            case 1 -> name;
            case 2 -> name.charAt(0) + MASK_CHAR;
            default -> name.charAt(0) + MASK_CHAR.repeat(length - 2) + name.charAt(length - 1);
        };
    }
    
    /**
     * 이메일 마스킹
     * 로컬 파트의 앞 3자리만 표시, 나머지는 마스킹
     * 예: abc***@example.com
     */
    public static String maskEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            return email;
        }
        
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return email;
        }
        
        String localPart = parts[0];
        String domain = parts[1];
        
        if (localPart.length() == 1) {
            return localPart + MASK_CHAR + "@" + domain;
        } else if (localPart.length() <= 3) {
            return localPart.charAt(0) + MASK_CHAR.repeat(localPart.length() - 1) + "@" + domain;
        }
        
        return localPart.substring(0, 3) + MASK_CHAR.repeat(localPart.length() - 3) + "@" + domain;
    }
    
    /**
     * 전화번호 마스킹
     * 중간 4자리를 마스킹 처리
     * 예: 010-****-5678
     */
    public static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return phoneNumber;
        }
        
        // 하이픈 제거
        String cleaned = phoneNumber.replaceAll("[^0-9]", "");
        
        if (cleaned.length() < 10) {
            return phoneNumber;
        }
        
        // 010-1234-5678 형식 (11자리)
        if (cleaned.length() == 11) {
            return cleaned.substring(0, 3) + "-" + MASK_CHAR.repeat(4) + "-" + cleaned.substring(7);
        }
        
        // 02-123-4567 형식 (10자리)
        if (cleaned.length() == 10) {
            if (cleaned.startsWith("02")) {
                return cleaned.substring(0, 2) + "-" + MASK_CHAR.repeat(3) + "-" + cleaned.substring(5);
            }
            return cleaned.substring(0, 3) + "-" + MASK_CHAR.repeat(3) + "-" + cleaned.substring(6);
        }
        
        return phoneNumber;
    }
    
    /**
     * 커스텀 마스킹
     * 지정된 시작/끝 인덱스 사이를 마스킹
     */
    public static String maskCustom(String value, int startIndex, int endIndex) {
        if (value == null || value.isBlank()) {
            return value;
        }
        
        if (startIndex < 0 || endIndex > value.length() || startIndex >= endIndex) {
            throw new IllegalArgumentException("Invalid masking range");
        }
        
        return value.substring(0, startIndex) 
            + MASK_CHAR.repeat(endIndex - startIndex) 
            + value.substring(endIndex);
    }
}