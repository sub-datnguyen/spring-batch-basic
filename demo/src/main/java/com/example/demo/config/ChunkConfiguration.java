package com.example.demo.config;

import com.example.demo.listener.ChunkLoggerListener;
import com.example.demo.model.Engineer;
import com.example.demo.model.Person;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class ChunkConfiguration {

    @Autowired
    private DataSource dataSource;

    @Bean(name = "jobExercise04AsChunk")
    public Job jobExercise04AsChunk(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("jobExercise04AsChunk", jobRepository)
                .start(exercise04(jobRepository, transactionManager))
                .build();
    }

    @Bean
    public Step exercise04(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("exercise04", jobRepository)
                .<Person, Person>chunk(10, transactionManager)
                .reader(reader())
//                .processor(validatePersonProcessor())
                .writer(personJdbcBatchItemWriter())
                .listener(new ChunkLoggerListener())
                .build();
    }

    @Bean
    public FlatFileItemReader<Person> reader() {
        FlatFileItemReader<Person> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("sample-data-error.csv"));
        reader.setLineMapper(new DefaultLineMapper<>() {
            {
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames("personId", "firstName", "lastName");
                    }
                });
                setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {
                    {
                        setTargetType(Person.class);
                    }
                });
            }
        });
        return reader;
    }

    @Bean
    public FlatFileItemWriter<Person> csvWriter () {

        BeanWrapperFieldExtractor<Person> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[] {"personId", "firstName", "lastName"});
        fieldExtractor.afterPropertiesSet();

        DelimitedLineAggregator<Person> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter("|");
        lineAggregator.setFieldExtractor(fieldExtractor);

        return  new FlatFileItemWriterBuilder<Person>()
                .name("itemWriter")
                .resource(new FileSystemResource("D:\\trainning\\Java-SpringBatch\\demo\\src\\main\\resources\\output.csv"))
//                .lineAggregator(new PassThroughLineAggregator<>())
                .lineAggregator(lineAggregator)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Person> personJdbcBatchItemWriter() {
        return new JdbcBatchItemWriterBuilder<Person>()
                .dataSource(dataSource)
                .sql("INSERT INTO person (person_id, first_name, last_name) VALUES (:personId, :firstName, :lastName)")
                .beanMapped()
                .build();
    }

    @Bean(name = "jobExercise05AsChunk")
    public Job jobExercise05AsChunk(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("jobExercise05AsChunk", jobRepository)
                .start(exercise05(jobRepository, transactionManager))
                .build();
    }

    @Bean
    public Step exercise05(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("exercise05", jobRepository)
                .<Person, Engineer>chunk(1, transactionManager)
                .reader(reader())
                .processor(validateEngineerProcessor())
                .writer(engineerJdbcBatchItemWriter())
                .listener(new ChunkLoggerListener())
                .faultTolerant()
//                .skip(DuplicateKeyException.class)
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Person> personJdbcCursorItemReader() {
        JdbcCursorItemReader<Person> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT person_id, first_name, last_name FROM person");
        reader.setRowMapper(new BeanPropertyRowMapper<>(Person.class));
        reader.setFetchSize(3);
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<Engineer> engineerJdbcBatchItemWriter() {
        return new JdbcBatchItemWriterBuilder<Engineer>()
                .dataSource(dataSource)
                .sql("INSERT INTO engineer (engineer_id, name) VALUES (:engineerId, :name)")
                .beanMapped()
                .build();
    }

    @Bean
    public ItemProcessor<Person, Engineer> validateEngineerProcessor() {
        return person -> {
            if (person.getLastName().startsWith("Super") && person.getPersonId() > 0) {
                Engineer engineer = new Engineer();
                engineer.setEngineerId(person.getPersonId());
                String name = person.getFirstName() + " " + person.getLastName();
                engineer.setName(name.substring(0, Math.min(name.length(), 40)));
                return engineer;
            } else {
                return null;
            }
        };
    }

//    @Bean
//    BeanValidatingItemProcessor<Person> beanValidatingItemProcessor() throws Exception {
//        BeanValidatingItemProcessor<Person> beanValidatingItemProcessor = new BeanValidatingItemProcessor<>();
//        beanValidatingItemProcessor.setFilter(true);
//        return beanValidatingItemProcessor;
//    }

    @Bean
    public ItemProcessor<Person, Person> validatePersonProcessor() {
        return item -> {
            if (item.getPersonId() > 0 && item.getFirstName().length() <= 20 && item.getLastName().length() <= 20) {
                return item;
            }
            // issue sonar when data nullable
            return null;
        };
    }

}