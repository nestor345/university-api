package com.university.administration.infrastructure.batch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.university.administration.infrastructure.batch.dto.GradeCsvDTO;
import com.university.administration.infrastructure.persistence.entity.Grade;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class GradeBatchConfig {

    private final ItemReader<GradeCsvDTO> reader;
    private final ItemProcessor<GradeCsvDTO, Grade> processor;
    private final ItemWriter<Grade> writer;

    @Bean
    public Job gradeBatchJob(JobRepository jobRepository, Step gradeStep) {
        return new JobBuilder("gradeBatchJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(gradeStep)
                .build();
    }

    @Bean
    public Step gradeStep(JobRepository jobRepository,
                          PlatformTransactionManager transactionManager) {

        return new StepBuilder("gradeStep", jobRepository)
                .<GradeCsvDTO, Grade>chunk(50, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skipLimit(Integer.MAX_VALUE)
                .skip(Exception.class)
                .build();
    }
}
