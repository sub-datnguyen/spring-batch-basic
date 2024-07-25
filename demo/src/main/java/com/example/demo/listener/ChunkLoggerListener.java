package com.example.demo.listener;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Component
public class ChunkLoggerListener implements ChunkListener {

    private static final Logger log = LoggerFactory.getLogger(ChunkLoggerListener.class);

    @Override
    public void beforeChunk(ChunkContext context) {
        log.info("Starting to process a new chunk: {}" + context.getStepContext().getStepName());
    }

    @Override
    public void afterChunk(ChunkContext context) {
        log.info("Number of items processed in the chunk: {}", context.getStepContext().getStepExecution().getReadCount());
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        log.error("Error occurred during chunk processing", context.getStepContext().getStepExecution().getFailureExceptions());
    }
}