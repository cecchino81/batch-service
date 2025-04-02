package it.batch.config;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import it.batch.batch.BookAuthorProcessor;
import it.batch.batch.BookTitleProcessor;
import it.batch.batch.BookWriter;
import it.batch.batch.RestBookReader;
import it.batch.copydb.BookDbProcessor;
import it.batch.copydb.BookDbReader;
import it.batch.copydb.BookDbWriter;
import it.batch.entity.BookEntity;
import it.batch.entity.NewBookEntity;

@Configuration
public class BatchConfig {
	
	@Autowired
	private BookDbReader bookDbReader;

	@Autowired
	private BookDbProcessor bookProcessor;

	@Autowired
	private BookDbWriter bookDbWriter;

	
	/* Questo è un Bean combinato, se voglio far eseguire i due JOB uno dopo l'altro. Se scommento questo, devo commentare i due JOB*/
	
  /*  @Bean
    Job combinedJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("combinedJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(chunkStep(jobRepository, transactionManager))  // Esegue il primo job (lettura da file)
                .next(copyBookStep(jobRepository, transactionManager)) // Dopo il completamento, esegue il secondo job (copia su DB)
                .build();
    }*/
	
	
    @Bean
    Job bookReaderJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("bookReadJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(chunkStep(jobRepository, transactionManager))
                .build();
    }

    @Bean
    Step chunkStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("bookReaderStep", jobRepository).<BookEntity, BookEntity>
                        chunk(10, transactionManager)
                .reader(reader())  // Ora sto chiamando il metodo reader() che sta giu per leggere dal file!
//                .reader(restBookReader()) // Cosi invece chiama restBookReader() per leggere dalla API esposta dal controller!
                .processor(processor())
                .writer(writer())
                .build();

    }

    @Bean
    @StepScope
    ItemReader<BookEntity> restBookReader() {
        return new RestBookReader("http://localhost:8080/book", new RestTemplate());
    }

    @Bean
    @StepScope
    ItemWriter<BookEntity> writer() {
        return new BookWriter();
    }

    @Bean
    @StepScope
    ItemProcessor<BookEntity, BookEntity> processor() {
        CompositeItemProcessor<BookEntity, BookEntity> processor = new CompositeItemProcessor<>();
        processor.setDelegates(List.of(new BookTitleProcessor(), new BookAuthorProcessor()));
        return processor;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<BookEntity> reader() {
        return new FlatFileItemReaderBuilder<BookEntity>()
                .name("bookReader")
                .resource(new ClassPathResource("book_data.csv"))
                .delimited()
                .names(new String[]{"title", "author", "year_of_publishing"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(BookEntity.class);
                }})
                .build();
    }
    
    @Bean
    Job copyBookJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("copyBookJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(copyBookStep(jobRepository, transactionManager))
                .build();
    }

    @Bean
    Step copyBookStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("copyBookStep", jobRepository).<BookEntity, NewBookEntity>chunk(10, transactionManager)
                .reader(bookDbReader)
                .processor(bookProcessor)
                .writer(bookDbWriter)
                .build();
    }
}
