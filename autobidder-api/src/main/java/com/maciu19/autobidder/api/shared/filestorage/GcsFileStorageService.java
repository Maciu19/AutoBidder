package com.maciu19.autobidder.api.shared.filestorage;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class GcsFileStorageService implements FileStorageService {

    private final static Logger log = LoggerFactory.getLogger(GcsFileStorageService.class);

    private final String bucketName;
    private final String credentialsLocation;
    private Storage storage;

    public GcsFileStorageService(
            @Value("${gcp.bucket.name}") String bucketName,
            @Value("${gcp.credentials.location}") String credentialsLocation
    ) {
        this.bucketName = bucketName;
        this.credentialsLocation = credentialsLocation;
    }

    @PostConstruct
    private void initializeGcs() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                new ClassPathResource(credentialsLocation).getInputStream()
        );
        this.storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
    }

    @Override
    public String uploadFile(MultipartFile file, String directory) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file.");
        }

        String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());
        String objectName = directory + "/" + uniqueFileName;

        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());

        return objectName;
    }

    @Override
    public void deleteFile(String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }

        String objectName = fileUrl.substring(("https://storage.googleapis.com/" + bucketName + "/").length());

        try {
            BlobId blobId = BlobId.of(bucketName, objectName);
            boolean deleted = storage.delete(blobId);
            if (deleted) {
                log.info("File deleted successfully from GCS: {} ", objectName);
            } else {
               log.info("File not found in GCS, nothing to delete: {} ", objectName);
            }
        } catch (Exception e) {
            throw new IOException("Error deleting file from GCS: " + objectName, e);
        }
    }

    public String generateSignedUrlForGet(String objectName) throws IOException {
        if (objectName == null || objectName.isBlank()) {
            throw new IOException("Cannot generate URL for an empty object name.");
        }

        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, objectName)).build();

        try {
            return storage.signUrl(blobInfo, 15, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature()).toString();
        } catch (Exception e) {
            throw new IOException("Failed to generate signed URL for object: " + objectName, e);
        }
    }

    private String generateUniqueFileName(String originalFilename) {
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID() + fileExtension;
    }
}
