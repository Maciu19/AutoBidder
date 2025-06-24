package com.maciu19.autobidder.api.shared.filestorage;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/signed-url")
    public ResponseEntity<?> getSignedUrl(@RequestParam String objectName) {
        try {
            String signedUrl = fileStorageService.generateSignedUrlForGet(objectName);
            return ResponseEntity.ok(Map.of("url", signedUrl));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Could not generate signed URL."));
        }
    }
}
