package com.yapps.senaempresa.service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String uploadFile(MultipartFile file) throws IOException;
    void deleteFile(String objectName);
    InputStream downloadFile(String objectName);
}
