package com.paulaccesso.visitor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class ImageService {
    
    @Value("${file.upload-dir:./visitors_images}")
    private String uploadDir;
    
    public String saveImage(MultipartFile file, String subDirectory) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        Path uploadPath = Paths.get(uploadDir, subDirectory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
            ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
            : ".jpg";
        String filename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(filename);
        
        Files.copy(file.getInputStream(), filePath);
        
        log.info("Image saved: {}", filePath);
        
        return subDirectory + "/" + filename;
    }
    
    public void deleteImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return;
        }
        
        try {
            Path path = Paths.get(uploadDir, imagePath);
            Files.deleteIfExists(path);
            log.info("Image deleted: {}", imagePath);
        } catch (IOException e) {
            log.error("Failed to delete image: {}", imagePath, e);
        }
    }
}