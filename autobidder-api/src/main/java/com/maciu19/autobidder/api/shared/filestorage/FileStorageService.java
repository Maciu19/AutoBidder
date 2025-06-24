package com.maciu19.autobidder.api.shared.filestorage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {

    String generateSignedUrlForGet(String objectName) throws IOException;

    String uploadFile(MultipartFile file, String directory) throws IOException;

    void deleteFile(String fileUrl) throws IOException;
}
