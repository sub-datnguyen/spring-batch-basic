package com.example.demo;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext springContext = new SpringApplication(Application.class).run(args);

        JobLauncher jobLauncher = springContext.getBean(JobLauncher.class);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Note: Use HH for 24-hour format
        String date = now.format(formatter);
        JobParameters jobParameters = new JobParametersBuilder()
//                .addString("Sync date", LocalDate.now().plusDays(3).toString())
                .addString("Sync date", date)
                .toJobParameters();
//        Job job4 = springContext.getBean("jobExercise04AsChunk", Job.class);
//        jobLauncher.run(job4, jobParameters);
//        Job job5 = springContext.getBean("jobExercise05AsChunk", Job.class);
//        jobLauncher.run(job5, jobParameters);

//        Job job6 = springContext.getBean("jobExercise06AsChunk", Job.class);
//        jobLauncher.run(job6, jobParameters);

//
//        Job job7 = springContext.getBean("jobExercise07AsChunk", Job.class);
//        jobLauncher.run(job7, jobParameters);
//
        Job job8 = springContext.getBean("jobExercise08AsChunk", Job.class);
        jobLauncher.run(job8, jobParameters);
    }
}
