package it.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableScheduling
@Slf4j
public class SchedulerConfig {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("bookReaderJob")  // Specifica quale Job iniettare
    private Job bookReaderJob;

    @Autowired
    @Qualifier("copyBookJob")  // Specifica l'altro Job
    private Job copyBookJob;

    @Scheduled(cron = "0 15 15 * * ?")  // Esegui alle 15:15 di ogni giorno, modifica come necessario
    public void scheduleJob() throws Exception {
        log.info("Job scheduler started to work");

        // Esegui il primo job (bookReaderJob)
        jobLauncher.run(bookReaderJob, new JobParametersBuilder()
                .addLong("uniqueness", System.nanoTime())  // Unicità dell'esecuzione
                .toJobParameters());

        log.info("bookReaderJob finished");

        // Esegui il secondo job (copyBookJob)
        jobLauncher.run(copyBookJob, new JobParametersBuilder()
                .addLong("uniqueness", System.nanoTime())  // Unicità dell'esecuzione
                .toJobParameters());

        log.info("copyBookJob finished");
        log.info("Job scheduler finished to work");
    }
}