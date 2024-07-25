package com.example.demo.tasklet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.example.demo.model.Person;
import com.example.demo.util.CSVUtils;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

@Component
public class LinesReader implements Tasklet {

    private final Logger logger = LoggerFactory
            .getLogger(LinesReader.class);

    private List<Person> persons = new ArrayList<>();

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {

        ClassPathResource inputResource = new ClassPathResource("sample-data.csv");

        try (InputStreamReader reader = new InputStreamReader(inputResource.getInputStream())) {
            CSVReader csvReader = new CSVReader(reader);
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                Person person = CSVUtils.fromPersonString(nextLine);
                if (person != null && person.getLastName().equalsIgnoreCase("doe")) {
                    persons.add(person);
                }
            }
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
        } catch (CsvValidationException | IOException e) {
            e.printStackTrace();
        }
        return RepeatStatus.FINISHED;
    }


    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        stepExecution
                .getJobExecution()
                .getExecutionContext()
                .put("persons", this.persons);
        logger.debug("Lines Reader ended.");
        return ExitStatus.COMPLETED;
    }
}