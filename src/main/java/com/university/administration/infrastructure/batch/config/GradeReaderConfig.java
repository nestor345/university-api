package com.university.administration.infrastructure.batch.config;

import com.university.administration.infrastructure.batch.dto.GradeCsvDTO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Slf4j
@Configuration
public class GradeReaderConfig {

    @Bean
    @StepScope
    public FlatFileItemReader<GradeCsvDTO> reader(
            @Value("#{jobParameters['archivoCsv']}") String archivoCsv,
            @Value("#{stepExecution.jobExecution.id}") Long jobExecutionId) {

        if (archivoCsv == null) {
            throw new IllegalArgumentException("El parámetro 'archivoCsv' no puede ser null");
        }

        MDC.put("batch.jobId", String.valueOf(jobExecutionId));

        log.info("[BATCH] Iniciando lectura archivo={}", archivoCsv);

        FlatFileItemReader<GradeCsvDTO> reader = new FlatFileItemReader<>();
        reader.setName("gradeFileReader");
        reader.setResource(new FileSystemResource(archivoCsv.replace("\\", "/")));
        reader.setLinesToSkip(1);
        reader.setEncoding("UTF-8");

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(";");
        tokenizer.setStrict(false);
        tokenizer.setNames(
                "studentId",
                "courseOfferingId",
                "gradeType",
                "gradeValue"
        );

        BeanWrapperFieldSetMapper<GradeCsvDTO> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(GradeCsvDTO.class);

        DefaultLineMapper<GradeCsvDTO> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(mapper);

        reader.setLineMapper(lineMapper);

        return reader;
    }
}
