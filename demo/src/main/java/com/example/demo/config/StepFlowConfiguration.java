package com.example.demo.config;

import com.example.demo.listener.BatchExecutionLogger;
import com.example.demo.listener.ChunkLoggerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StepFlowConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ChunkLoggerListener.class);

    @Bean(name = "jobExercise06AsChunk")
    public Job jobExercise06AsChunk(JobRepository jobRepository, Step step01, Step step02, Step markupStep, Step cleanupStep) {
        return new JobBuilder("jobExercise06AsChunk", jobRepository)
                .start(step01)
                .on(ExitStatus.COMPLETED.getExitCode()).to(step02)
                .from(step01).on("EXIST_CUSTOM").to(markupStep)
                .from(step02).on("*").to(cleanupStep)
                .end()
                .listener(new JobExecutionListener() {
                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        BatchStatus status = jobExecution.getStatus();
                        if (status == BatchStatus.COMPLETED) {
                            System.out.println("Job đã hoàn thành thành công: " + status);
                        } else {
                            System.out.println("Job đã thất bại với trạng thái: " + status);
                        }
                    }
                })
                .build();
    }

    @Bean(name = "jobExercise07AsChunk")
    public Job jobExercise07AsChunk(JobRepository jobRepository, Step step01) {
        return new JobBuilder("jobExercise07AsChunk", jobRepository)
                .start(step01).on(ExitStatus.COMPLETED.getExitCode()).fail()
                .end()
                .listener(new JobExecutionListener() {
                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        BatchStatus status = jobExecution.getStatus();
                        if (status == BatchStatus.COMPLETED) {
                            System.out.println("Job đã hoàn thành thành công!");
                        } else {
                            System.out.println("Job đã thất bại với trạng thái: " + status);
                        }
                    }
                })
                .build();
    }

    @Bean
    public Step step01(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step01", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("Running step 01");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .listener(new StepExecutionListener() {
                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        return ExitStatus.COMPLETED;
                    }
                })
                .build();
    }

    @Bean
    public Step cleanupStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("cleanupStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("Running clean up step");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step markupStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("markupStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("Running mark up step");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step step02(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step02", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("Running step 02");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step step03(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step03", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("Running step 03");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean(name = "jobExercise08AsChunk")
    public Job jobExercise08AsChunk(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                    Step step04, Step step05) {
        return new JobBuilder("jobExercise08AsChunk", jobRepository)
                .listener(new BatchExecutionLogger())
                .start(step04).on(ExitStatus.FAILED.getExitCode()).end()
                .end()
                .build();
    }

    @Bean
    public Step step04(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step04", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("Running step 04");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .listener(new StepExecutionListener() {
                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        return ExitStatus.FAILED;
                    }
                })
                .build();
    }

    @Bean
    public Step step05(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step05", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("Running step 05");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .listener(new StepExecutionListener() {
                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        return ExitStatus.FAILED;
                    }
                })
                .build();
    }
}