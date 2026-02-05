package com.university.administration.service.impl;

import com.university.administration.service.FileStorageService;
import com.university.administration.service.ProcesoBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcesoBatchServiceImpl implements ProcesoBatchService {
    private final JobLauncher jobLauncher;
    private final Job usuarioBatchJob;
    private final FileStorageService fileStorageService;

    @Override
    public String crearYLanzarProceso(MultipartFile file) {
        long start = System.currentTimeMillis();
        String txId = MDC.get("transactionId");
        try {
            Path archivoCsv = fileStorageService.guardar(file);

            JobParameters params = new JobParametersBuilder()
                    .addString("transactionId", txId)
                    .addString("archivoCsv", archivoCsv.toString())
                    .toJobParameters();

            new Thread(() -> {
                long asyncStart = System.currentTimeMillis();
                try {
                    jobLauncher.run(usuarioBatchJob, params);

                    log.info("[JOB-END] gradesBatch durationMs={}",
                            System.currentTimeMillis() - asyncStart);

                } catch (Exception e) {
                    log.error("[JOB-FAIL] usuariosBatch error={} durationMs={}",
                            e.getMessage(),
                            System.currentTimeMillis() - asyncStart,
                            e);

                    throw new RuntimeException("No fue posible ejecutar el batch", e);
                }
            }).start();

            log.info("[END] crearYLanzarProceso durationMs={}",
                    System.currentTimeMillis() - start);

            return "Batch started successfully";

        } catch (Exception e) {
            log.error("[FAIL] crearYLanzarProceso error={}",
                    e.getMessage(),
                    e);
            throw e;
        }
    }
    @Async
    public void lanzarJobAsync(Long idProceso, Path archivoCsv) {
        long start = System.currentTimeMillis();
        log.info("[START-ASYNC] lanzarJobAsync idProceso={} archivo={}", idProceso, archivoCsv);

        JobParameters params = new JobParametersBuilder()
                .addLong("idProceso", idProceso)
                .addString("archivoCsv", archivoCsv.toString())
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        try {
            jobLauncher.run(usuarioBatchJob, params);

            log.info("[END-ASYNC] lanzarJobAsync idProceso={}",
                    idProceso);

        } catch (JobExecutionAlreadyRunningException |
                 JobRestartException |
                 JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {

            log.error("[FAIL-ASYNC] lanzarJobAsync idProceso={} error={}",
                    idProceso, e.getMessage(), e);
        }
    }
}
