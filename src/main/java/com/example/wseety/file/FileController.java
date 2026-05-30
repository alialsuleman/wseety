package com.example.wseety.file;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'USER')")
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;


    //
    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadFiles(
            @RequestParam("files") List<MultipartFile> files
    ) throws IOException {
        List<String> storedFiles = fileStorageService.saveFiles(files);
        return ResponseEntity.ok(storedFiles);
    }



    // حذف صورة
//    @DeleteMapping("/deletepostimage/{id}")
//    public ResponseEntity<ApiResponse> deleteFile(@PathVariable Integer id) {
//        System.out.println(id);
//        boolean deleted = fileStorageService.deleteFile(id);
//
//        ApiResponse response = ApiResponse.builder()
//                .success(true)
//                .message("created successfully")
//                .timestamp(LocalDateTime.now())
//                .status(HttpStatus.OK.value())
//                .data (null)
//                .build();
//
//        if (deleted) {
//            response.setMessage("Deleted successfully");
//            return ResponseEntity.ok(response);
//        } else {
//            response.setMessage("File not found");
//            response.setStatus(HttpStatus.BAD_REQUEST.value());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
}
