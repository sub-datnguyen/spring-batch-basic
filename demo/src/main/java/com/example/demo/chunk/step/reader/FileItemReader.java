package com.example.demo.chunk.step.reader;

import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.example.demo.model.Person;
import com.example.demo.util.CSVUtils;
import com.opencsv.CSVReader;

@Component
public class FileItemReader implements ItemReader<Person> {
    private final Logger logger = LoggerFactory.getLogger(FileItemReader.class);

    private CSVReader csvReader;

    private boolean resourceOpened = false;

    @Override
    public Person read() throws Exception {
        if (!resourceOpened) {
            openCSVReader();
            resourceOpened = true;
        }

        String[] nextLine;
        try {
            if ((nextLine = csvReader.readNext()) != null) {
                return CSVUtils.fromPersonString(nextLine);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private void openCSVReader() throws IOException {
        ClassPathResource inputResource = new ClassPathResource("sample-data.csv");
        InputStreamReader reader = new InputStreamReader(inputResource.getInputStream());
        csvReader = new CSVReader(reader);
    }

    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        try {
            if (csvReader != null) {
                csvReader.close();
            }
        } catch (IOException e) {
            logger.warn("Error close CSV reader", e);
        }
        return ExitStatus.COMPLETED;
    }
}
