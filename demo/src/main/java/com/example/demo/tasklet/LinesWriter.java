package com.example.demo.tasklet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.example.demo.model.Person;
import com.example.demo.util.CSVUtils;

@Component
public class LinesWriter implements Tasklet {

    private List<Person> persons;
    private final Logger logger = LoggerFactory.getLogger(LinesWriter.class);

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        ExecutionContext executionContext = stepExecution
                .getJobExecution()
                .getExecutionContext();
//        this.persons = (List<Person>) executionContext.get("persons");
        logger.debug("Lines Writer initialized.");
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
        this.persons = (List<Person>)  stepContribution.getStepExecution().getExecutionContext().get("persons");
        try {
            File outputFile = CSVUtils.getFile();

            FileOutputStream fos = new FileOutputStream(outputFile);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));

            for (Person person : persons) {
                logger.debug("Wrote line " + person.toString());
                writer.write(CSVUtils.toPersonString(person));
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return RepeatStatus.FINISHED;
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("Lines Writer ended.");
        return ExitStatus.COMPLETED;
    }
}