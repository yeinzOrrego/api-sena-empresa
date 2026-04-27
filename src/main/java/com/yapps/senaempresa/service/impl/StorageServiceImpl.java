package com.yapps.senaempresa.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.yapps.senaempresa.config.OciProperties;
import com.yapps.senaempresa.service.StorageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final ObjectStorageClient storageClient;
    private final OciProperties props;

    public Map<String, String> uploadFile(MultipartFile file) throws IOException {
        String objectName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        PutObjectRequest request = PutObjectRequest.builder()
                .namespaceName(props.getNamespace())
                .bucketName(props.getBucketName())
                .objectName(objectName)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .putObjectBody(file.getInputStream())
                .build();

        storageClient.putObject(request);

        return Map.of(
                "objectName", objectName,
                "url", buildPublicUrl(objectName));
    }

    public void deleteFile(String objectName) throws IOException {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .namespaceName(props.getNamespace())
                .bucketName(props.getBucketName())
                .objectName(objectName)
                .build();

        storageClient.deleteObject(request);
    }

    public InputStream downloadFile(String objectName) {
        GetObjectRequest request = GetObjectRequest.builder()
                .namespaceName(props.getNamespace())
                .bucketName(props.getBucketName())
                .objectName(objectName)
                .build();

        return storageClient.getObject(request).getInputStream();
    }

    private String buildPublicUrl(String objectName) {
        return storageClient.getEndpoint()
                + "/n/" + props.getNamespace()
                + "/b/" + props.getBucketName()
                + "/o/" + objectName;
    }
}
