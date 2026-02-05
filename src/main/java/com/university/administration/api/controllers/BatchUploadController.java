package com.university.administration.api.controllers;

import com.university.administration.service.ProcesoBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/batch")
@RequiredArgsConstructor
public class BatchUploadController {

    private final ProcesoBatchService service;

    @PostMapping("/upload-grades")
    public ResponseEntity<String> uploadGrades(
            @RequestParam("file") MultipartFile file) throws Exception {
        String response = service.crearYLanzarProceso(file);

        return ResponseEntity.ok(response);
    }
}
