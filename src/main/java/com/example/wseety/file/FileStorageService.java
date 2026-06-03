package com.example.wseety.file;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path rootLocation;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir);
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    // ============================================================
    // 1. Upload file (with optional sub folder)
    // ============================================================
    public String uploadFile(MultipartFile file, String subFolder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or missing");
        }

        try {
            Path targetDir = (subFolder != null && !subFolder.isBlank())
                    ? rootLocation.resolve(subFolder)
                    : rootLocation;

            Files.createDirectories(targetDir);

            String originalName = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = getExtension(originalName);
            String fileName = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);

            Path destination = targetDir.resolve(fileName);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            // Return relative path to store in DB
            return (subFolder != null && !subFolder.isBlank())
                    ? subFolder + "/" + fileName
                    : fileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    // Overload without subFolder
    public String uploadFile(MultipartFile file) {
        return uploadFile(file, null);
    }


    // ============================================================
    // 2. Upload file with type validation
    // ============================================================

    public String uploadImage(MultipartFile file, String subFolder) {
        validateFileType(file, List.of("image/jpeg", "image/png", "image/webp", "image/gif"));
        return uploadFile(file, subFolder != null ? subFolder : "images");
    }

    public String uploadDocument(MultipartFile file, String subFolder) {
        validateFileType(file, List.of(
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "text/plain"
        ));
        return uploadFile(file, subFolder != null ? subFolder : "documents");
    }

    // Generic upload with custom allowed MIME types
    public String uploadWithTypeCheck(MultipartFile file, List<String> allowedMimeTypes, String subFolder) {
        validateFileType(file, allowedMimeTypes);
        return uploadFile(file, subFolder);
    }


    // ============================================================
    // 3. Download file
    // ============================================================
    public Resource downloadFile(String relativePath) {
        try {
            Path filePath = rootLocation.resolve(relativePath).normalize();

            // Protect against Path Traversal attacks (e.g. ../../etc/passwd)
            if (!filePath.startsWith(rootLocation)) {
                throw new SecurityException("Access outside storage directory is not allowed");
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File not found or not readable: " + relativePath);
            }

            return resource;

        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid file path: " + relativePath, e);
        }
    }

    // Download with content type and file name metadata
    public Map<String, Object> downloadFileWithMeta(String relativePath) {
        Resource resource = downloadFile(relativePath);
        String contentType;

        try {
            Path filePath = rootLocation.resolve(relativePath).normalize();
            contentType = Files.probeContentType(filePath);
        } catch (IOException e) {
            contentType = "application/octet-stream";
        }

        return Map.of(
                "resource", resource,
                "contentType", contentType != null ? contentType : "application/octet-stream",
                "fileName", Paths.get(relativePath).getFileName().toString()
        );
    }


    // ============================================================
    // 4. Delete file
    // ============================================================
    public void deleteFile(String relativePath) {
        try {
            Path filePath = rootLocation.resolve(relativePath).normalize();

            // Protect against Path Traversal attacks
            if (!filePath.startsWith(rootLocation)) {
                throw new SecurityException("Access outside storage directory is not allowed");
            }

            boolean deleted = Files.deleteIfExists(filePath);

            if (!deleted) {
                throw new RuntimeException("File not found: " + relativePath);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + e.getMessage(), e);
        }
    }

    // Delete without throwing exception if file does not exist
    public boolean deleteFileSilently(String relativePath) {
        try {
            Path filePath = rootLocation.resolve(relativePath).normalize();
            if (!filePath.startsWith(rootLocation)) return false;
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }


    // ============================================================
    // Helper Methods
    // ============================================================

    public void validateFileType(MultipartFile file, List<String> allowedTypes) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();

        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new IllegalArgumentException(
                    "File type not allowed: " + contentType +
                            ". Allowed types: " + allowedTypes
            );
        }
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}