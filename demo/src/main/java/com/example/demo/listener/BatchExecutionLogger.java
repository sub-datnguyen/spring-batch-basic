package com.example.demo.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.stereotype.Component;

@Component
public class BatchExecutionLogger implements JobExecutionListener, StepExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(BatchExecutionLogger.class);

    private long jobStartTime;
    private long stepStartTime;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        jobStartTime = System.currentTimeMillis();
        log.info("Job '{}' started at {}", jobExecution.getJobInstance().getJobName(), jobStartTime);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        long jobEndTime = System.currentTimeMillis();
        log.info("Job '{}' finished at {} with status: {}",
                jobExecution.getJobInstance().getJobName(), jobEndTime, jobExecution.getStatus());
        log.info("Total job execution time: {} ms", jobEndTime - jobStartTime);

        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("Job '{}' failed. See previous logs for error details.", jobExecution.getJobInstance().getJobName());
        }
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        stepStartTime = System.currentTimeMillis();
        log.info("Step '{}' started at {}", stepExecution.getStepName(), stepStartTime);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        long stepEndTime = System.currentTimeMillis();
        log.info("Step '{}' finished at {} with status: {}",
                stepExecution.getStepName(), stepEndTime, stepExecution.getStatus());
        log.info("Total step execution time: {} ms", stepEndTime - stepStartTime);

        if (stepExecution.getStatus() == BatchStatus.FAILED) {
            log.error("Step '{}' failed. See previous logs for error details.", stepExecution.getStepName());
            return ExitStatus.FAILED;
        }
        return ExitStatus.COMPLETED;
    }
}
