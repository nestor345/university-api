package com.university.administration.service.impl;

import com.university.administration.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${batch.files.base-path}")
    private String basePath;


    public FileStorageServiceImpl() {
    }

    @Override
    @Transactional
    public Path guardar(MultipartFile archivo) {
        UUID id = UUID.randomUUID();

        long start = System.currentTimeMillis();
        log.info("[START] guardarArchivo  nombreOriginal={} sizeBytes={}",
                archivo.getOriginalFilename(),
                archivo.getSize()
        );

        if (archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        try {
            String nombreOriginal = StringUtils.cleanPath(archivo.getOriginalFilename());

            if (nombreOriginal.contains("..")) {
                throw new IllegalArgumentException("Nombre de archivo inválido");
            }

            String extension = StringUtils.getFilenameExtension(nombreOriginal);
            String nombreSeguro = "archivo_" + System.currentTimeMillis() +
                    (extension != null ? "." + extension : "");

            Path baseDir = Paths.get(basePath).toAbsolutePath().normalize();
            Path directorioProceso = baseDir.resolve("proceso_" + id).normalize();

            Files.createDirectories(directorioProceso);

            Path destino = directorioProceso.resolve(nombreSeguro).normalize();

            if (!destino.startsWith(baseDir)) {
                throw new SecurityException("Intento de Path Traversal detectado");
            }

            archivo.transferTo(destino.toFile());


            log.info("[END] guardarArchivo nombreAlmacenado={} path={} durationMs={}",
                    nombreSeguro,
                    destino,
                    System.currentTimeMillis() - start
            );

            return destino;

        } catch (IOException e) {
            throw new RuntimeException("Error guardando el archivo del proceso batch", e);
        }
    }

}


