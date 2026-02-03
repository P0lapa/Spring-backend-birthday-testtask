package com.congratulator.beta.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class PhotoService {

    @Value("${app.upload-dir:./uploads}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Created upload directory: {}", uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public String savePhoto(MultipartFile photo) {
        if (photo == null || photo.isEmpty()) {
            throw new IllegalArgumentException("Photo is empty");
        }

        try {
            String originalFilename = photo.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = UUID.randomUUID() + fileExtension;

            Path targetPath = Paths.get(uploadDir).resolve(uniqueFilename).normalize();
            Files.copy(photo.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            log.info("Photo saved: {}", targetPath);
            return uniqueFilename; // Возвращаем только имя файла
        } catch (IOException e) {
            throw new RuntimeException("Failed to save photo", e);
        }
    }

    public void deletePhoto(String photoPath) {
        if (photoPath == null) {
            return;
        }
        try {
            Path filePath = Paths.get(uploadDir).resolve(photoPath).normalize();
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.info("Deleted photo: {}", filePath);
            }
        } catch (IOException e) {
            log.warn("Failed to delete photo: {}", photoPath, e);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null) {
            return ".jpg";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex);
        }
        return ".jpg";
    }
}