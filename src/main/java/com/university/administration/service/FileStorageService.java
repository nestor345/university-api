package com.university.administration.service;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileStorageService {
    Path guardar(MultipartFile archivo);
}
