package com.example.wseety.file;


import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam MultipartFile file,
                                         @RequestParam(required = false) String folder) {
        String path = fileStorageService.uploadFile(file, folder);
        return ResponseEntity.ok(path);
    }

    @PostMapping("/upload/image")
    public ResponseEntity<String> uploadImage(@RequestParam MultipartFile file,
                                              @RequestParam(required = false) String folder) {
        String path = fileStorageService.uploadImage(file, folder);
        return ResponseEntity.ok(path);
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam String path) {
        Map<String, Object> meta = fileStorageService.downloadFileWithMeta(path);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType((String) meta.get("contentType")))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + meta.get("fileName") + "\"")
                .body((Resource) meta.get("resource"));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam String path) {
        fileStorageService.deleteFile(path);
        return ResponseEntity.noContent().build();
    }
}