package com.learning.englishpro.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Serves files from the local upload directory as static resources.
 *
 * <p>A file stored at {@code uploads/thumbnails/course_1_abc.jpg} will be
 * accessible via {@code GET /files/thumbnails/course_1_abc.jpg}.
 */
@Configuration
public class FileResourceConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absoluteUploadPath = Paths.get(uploadDir).toAbsolutePath().toUri().toString();

        registry.addResourceHandler("/files/**")
                .addResourceLocations(absoluteUploadPath);
    }
}
