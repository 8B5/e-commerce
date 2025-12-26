package com.ecommerce.product.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 파일 저장 서비스 인터페이스
 * DIP 원칙에 따라 구현체를 교체 가능하도록 설계
 * (LocalFileStorage -> S3FileStorage 등)
 */
public interface FileStorageService {
    
    /**
     * 단일 파일 업로드
     * @param file 업로드할 파일
     * @param directory 저장할 디렉토리 (예: "products", "reviews")
     * @return 저장된 파일의 URL
     */
    String uploadFile(MultipartFile file, String directory);
    
    /**
     * 다중 파일 업로드
     * @param files 업로드할 파일들
     * @param directory 저장할 디렉토리
     * @return 저장된 파일들의 URL 목록
     */
    List<String> uploadFiles(List<MultipartFile> files, String directory);
    
    /**
     * 파일 삭제
     * @param fileUrl 삭제할 파일의 URL
     * @return 삭제 성공 여부
     */
    boolean deleteFile(String fileUrl);
    
    /**
     * 다중 파일 삭제
     * @param fileUrls 삭제할 파일들의 URL 목록
     * @return 삭제 성공한 파일 개수
     */
    int deleteFiles(List<String> fileUrls);
    
    /**
     * 파일 존재 여부 확인
     * @param fileUrl 확인할 파일의 URL
     * @return 파일 존재 여부
     */
    boolean fileExists(String fileUrl);
    
    /**
     * 파일 크기 제한 확인
     * @param file 확인할 파일
     * @return 크기 제한 통과 여부
     */
    boolean isFileSizeValid(MultipartFile file);
    
    /**
     * 파일 확장자 확인
     * @param file 확인할 파일
     * @return 허용된 확장자 여부
     */
    boolean isFileExtensionValid(MultipartFile file);
}