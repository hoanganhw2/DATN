package com.learning.englishpro.common.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    /**
     * Stores a file in the given subdirectory.
     * @param file the multipart file to store
     * @param subDir the subdirectory inside the base storage
     * @param fileName the customized name for the stored file
     * @return the relative path/URL to the stored file
     */
    String store(MultipartFile file, String subDir, String fileName);
    
    /**
     * Deletes a file given its path/URL.
     * @param filePath the path/URL returned by store
     */
    void delete(String filePath);
}
