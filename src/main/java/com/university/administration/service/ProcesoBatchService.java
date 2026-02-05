package com.university.administration.service;

import org.springframework.web.multipart.MultipartFile;

public interface ProcesoBatchService {
    String crearYLanzarProceso(MultipartFile file);
}
