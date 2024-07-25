package com.example.demo.chunk.step.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.example.demo.model.Person;
import com.example.demo.util.CSVUtils;

@Component
public class FileItemWriter implements ItemWriter<Person> {

    private final Logger logger = LoggerFactory.getLogger(FileItemWriter.class);

    @Override
    public void write(Chunk<? extends Person> chunk) throws Exception {
        try {
            File outputFile = CSVUtils.getFile();

            FileOutputStream fos = new FileOutputStream(outputFile);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));

            for (Person person : chunk.getItems()) {
                writer.write(CSVUtils.toPersonString(person));
                logger.info("Wrote line: " + person);
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
