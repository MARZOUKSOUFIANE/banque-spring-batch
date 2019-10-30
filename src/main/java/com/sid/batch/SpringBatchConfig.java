package com.sid.batch;

import com.sid.entities.BankTransaction;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {
    @Autowired private JobBuilderFactory jobBuilderFactory;
    @Autowired private StepBuilderFactory stepBuilderFactory;
    @Autowired private ItemReader<BankTransaction> itemReader;
    @Autowired private ItemWriter<BankTransaction> bankTransactionItemWriter;


    @Bean
    public Job bankJob(){
        Step step1=stepBuilderFactory.get("step-load-data")
                .<BankTransaction,BankTransaction>chunk(100)
                .reader(itemReader)
                .processor(compositeItemsProcessor())
                .writer(bankTransactionItemWriter)
                .build();

        return jobBuilderFactory.get("bank-data-loader-job")
                .start(step1)
                .build();
    }

    @Bean
    public ItemProcessor<? super BankTransaction,? extends BankTransaction> compositeItemsProcessor() {
        List<ItemProcessor<BankTransaction,BankTransaction>> listProcessors=new ArrayList<>();
        listProcessors.add(itemProcessor1());
        listProcessors.add(itemProcessor2());
        CompositeItemProcessor<BankTransaction,BankTransaction> compositeItemProcessor=new CompositeItemProcessor<>();
        compositeItemProcessor.setDelegates(listProcessors);
        return compositeItemProcessor;
    }

    @Bean
    BankTransactionItemProcessor itemProcessor1(){
        return new BankTransactionItemProcessor();
    }

    @Bean
    BankTransactionItemAnalyticsProcessor itemProcessor2(){
        return new BankTransactionItemAnalyticsProcessor();
    }

    @Bean
    public FlatFileItemReader<BankTransaction> flatFileItemReader(@Value("${inputFile}") Resource resource){
        FlatFileItemReader<BankTransaction> fileItemReader=new FlatFileItemReader<>();
        fileItemReader.setName("FFIR1");
        fileItemReader.setLinesToSkip(1);
        fileItemReader.setResource(resource);
        fileItemReader.setLineMapper(lineMapper());
        return fileItemReader;
    }

    @Bean
    public LineMapper<BankTransaction> lineMapper() {
        DefaultLineMapper<BankTransaction> lineMapper=new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer=new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id","account_id","strTransactionDate","transactionType","amount");
        lineMapper.setLineTokenizer(lineTokenizer);
        BeanWrapperFieldSetMapper<BankTransaction> fieldSetMapper=new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(BankTransaction.class);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public ItemProcessor<BankTransaction,BankTransaction> itemProcessor(){
        return new BankTransactionItemProcessor();
      }

    @Bean
    public ItemWriter<BankTransaction> itemWriter(){
        return new BankTransactionItemWriter();
    }


}
