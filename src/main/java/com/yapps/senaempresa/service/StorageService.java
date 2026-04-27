package com.yapps.senaempresa.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    Map<String, String> uploadFile(MultipartFile file) throws IOException;
    void deleteFile(String objectName) throws IOException;
    InputStream downloadFile(String objectName);
}
