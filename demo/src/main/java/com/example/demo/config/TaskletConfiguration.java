package com.example.demo.config;

import com.example.demo.model.Person;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.demo.tasklet.LinesReader;
import com.example.demo.tasklet.LinesWriter;


public class TaskletConfiguration {

    @Bean
    @JobScope
    public Job jobAsTasklet(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("simpleJob", jobRepository)
                .start(readLines(jobRepository, transactionManager))
//                .next(writeLines(jobRepository, transactionManager))
                .build();
    }

    @Bean
    @StepScope
    public Step readLines(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("readLines", jobRepository)
                .tasklet(new LinesReader(), transactionManager)
                .build();
    }

    @Bean
    public Step writeLines(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("writeLines", jobRepository)
                .tasklet(new LinesWriter(), transactionManager)
                .build();
    }

    @Bean
    public Step processerLines(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("writeLines", jobRepository)
                .tasklet(new LinesWriter(), transactionManager)
                .build();
    }
}