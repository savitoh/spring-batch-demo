package com.br.savit0h.batchservicedemo.configuration;

import com.br.savit0h.batchservicedemo.dto.User;
import com.br.savit0h.batchservicedemo.listener.JobCompletionNotificationListener;
import com.br.savit0h.batchservicedemo.processor.UserItemProcessor;
import com.br.savit0h.batchservicedemo.reader.CustomReader;
import com.br.savit0h.batchservicedemo.writer.CustomWriter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;


@Configuration
@EnableBatchProcessing
public class BatchConfiguration extends JobExecutionListenerSupport {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final CustomReader customReader;

    private final CustomWriter customWriter;

    @Autowired
    public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
                              CustomReader customReader, CustomWriter customWriter) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.customReader = customReader;
        this.customWriter = customWriter;
    }


    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("import-user-job")
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<User, User> chunk(10)
                .reader(customReader.readerUsers())
                .processor(new UserItemProcessor())
                .writer(customWriter)
                .build();
    }

}
