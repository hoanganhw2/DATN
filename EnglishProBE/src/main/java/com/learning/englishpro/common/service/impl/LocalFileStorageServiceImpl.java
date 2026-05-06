package com.learning.englishpro.common.service.impl;

import com.learning.englishpro.common.exception.AppException;
import com.learning.englishpro.common.exception.ErrorCode;
import com.learning.englishpro.common.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
public class LocalFileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public String store(MultipartFile file, String subDir, String fileName) {
        try {
            Path targetLocation = Paths.get(uploadDir).resolve(subDir).normalize();
            Files.createDirectories(targetLocation);

            Path targetFile = targetLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

            return subDir + "/" + fileName; // Returns relative path (e.g. avatars/1_1234567.jpg)
        } catch (IOException ex) {
            log.error("Không thể lưu file {}. Vui lòng thử lại!", fileName, ex);
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public void delete(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return;
        }
        try {
            Path fileToDelete = Paths.get(uploadDir).resolve(filePath).normalize();
            Files.deleteIfExists(fileToDelete);
        } catch (IOException ex) {
            log.error("Không thể xóa file: {}", filePath, ex);
        }
    }
}
