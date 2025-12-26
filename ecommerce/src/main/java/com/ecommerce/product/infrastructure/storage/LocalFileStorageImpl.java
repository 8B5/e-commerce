package com.ecommerce.product.infrastructure.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 로컬 파일 시스템 기반 파일 저장 구현체
 * 개발 환경에서 사용하며, 운영 환경에서는 S3FileStorageImpl로 교체 예정
 */
@Slf4j
@Service
public class LocalFileStorageImpl implements FileStorageService {
    
    private final String uploadPath;
    private final String baseUrl;
    private final long maxFileSize;
    private final List<String> allowedExtensions;
    
    public LocalFileStorageImpl(
            @Value("${app.file.upload-path:/app/uploads}") String uploadPath,
            @Value("${app.file.base-url:http://localhost:8080/api/files}") String baseUrl,
            @Value("${app.file.max-size:10485760}") long maxFileSize, // 10MB
            @Value("${app.file.allowed-extensions:jpg,jpeg,png,gif,webp}") String allowedExtensions) {
        this.uploadPath = uploadPath;
        this.baseUrl = baseUrl;
        this.maxFileSize = maxFileSize;
        this.allowedExtensions = Arrays.asList(allowedExtensions.toLowerCase().split(","));
        
        // 업로드 디렉토리 생성
        createDirectoryIfNotExists(Paths.get(uploadPath));
    }
    
    @Override
    public String uploadFile(MultipartFile file, String directory) {
        validateFile(file);
        
        try {
            String fileName = generateFileName(file.getOriginalFilename());
            Path directoryPath = Paths.get(uploadPath, directory);
            createDirectoryIfNotExists(directoryPath);
            
            Path filePath = directoryPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            String fileUrl = baseUrl + "/" + directory + "/" + fileName;
            log.info("파일 업로드 성공: {}", fileUrl);
            
            return fileUrl;
            
        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }
    
    @Override
    public List<String> uploadFiles(List<MultipartFile> files, String directory) {
        List<String> uploadedUrls = new ArrayList<>();
        
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String fileUrl = uploadFile(file, directory);
                uploadedUrls.add(fileUrl);
            }
        }
        
        return uploadedUrls;
    }
    
    @Override
    public boolean deleteFile(String fileUrl) {
        try {
            Path filePath = getFilePathFromUrl(fileUrl);
            boolean deleted = Files.deleteIfExists(filePath);
            
            if (deleted) {
                log.info("파일 삭제 성공: {}", fileUrl);
            } else {
                log.warn("파일이 존재하지 않음: {}", fileUrl);
            }
            
            return deleted;
            
        } catch (IOException e) {
            log.error("파일 삭제 실패: {}", fileUrl, e);
            return false;
        }
    }
    
    @Override
    public int deleteFiles(List<String> fileUrls) {
        int deletedCount = 0;
        
        for (String fileUrl : fileUrls) {
            if (deleteFile(fileUrl)) {
                deletedCount++;
            }
        }
        
        return deletedCount;
    }
    
    @Override
    public boolean fileExists(String fileUrl) {
        try {
            Path filePath = getFilePathFromUrl(fileUrl);
            return Files.exists(filePath);
        } catch (Exception e) {
            log.error("파일 존재 여부 확인 실패: {}", fileUrl, e);
            return false;
        }
    }
    
    @Override
    public boolean isFileSizeValid(MultipartFile file) {
        return file.getSize() <= maxFileSize;
    }
    
    @Override
    public boolean isFileExtensionValid(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return false;
        }
        
        String extension = getFileExtension(originalFilename).toLowerCase();
        return allowedExtensions.contains(extension);
    }
    
    /**
     * 파일 유효성 검증
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일은 업로드할 수 없습니다.");
        }
        
        if (!isFileSizeValid(file)) {
            throw new IllegalArgumentException("파일 크기가 제한을 초과했습니다. (최대: " + maxFileSize + " bytes)");
        }
        
        if (!isFileExtensionValid(file)) {
            throw new IllegalArgumentException("허용되지 않은 파일 확장자입니다. 허용 확장자: " + allowedExtensions);
        }
    }
    
    /**
     * 고유한 파일명 생성
     */
    private String generateFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        return timestamp + "_" + uuid + "." + extension;
    }
    
    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
    
    /**
     * 디렉토리 생성
     */
    private void createDirectoryIfNotExists(Path directory) {
        try {
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
                log.info("디렉토리 생성: {}", directory);
            }
        } catch (IOException e) {
            log.error("디렉토리 생성 실패: {}", directory, e);
            throw new RuntimeException("디렉토리 생성에 실패했습니다.", e);
        }
    }
    
    /**
     * URL에서 파일 경로 추출
     */
    private Path getFilePathFromUrl(String fileUrl) {
        String relativePath = fileUrl.replace(baseUrl + "/", "");
        return Paths.get(uploadPath, relativePath);
    }
}