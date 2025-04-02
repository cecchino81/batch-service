package it.batch.controller;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
public class JobLauncherController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("bookReaderJob") 
    private Job bookReaderJob;  // Primo job (legge i dati da csv e scrive sulla tabella book)

    @Autowired
    @Qualifier("copyBookJob")
    private Job copyBookJob;  // Secondo job (copia i dati da book su new-book nel DB)

    @PostMapping("/start-book-reader-job")
    public ResponseEntity<String> startBookReaderJob() {
        return startJob(bookReaderJob, "bookReaderJob");
    }

    @PostMapping("/start-copy-job")
    public ResponseEntity<String> startCopyBookJob() {
        return startJob(copyBookJob, "copyBookJob");
    }

    private ResponseEntity<String> startJob(Job job, String jobName) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())  // Unicità dell'esecuzione
                    .toJobParameters();

            jobLauncher.run(job, jobParameters);
            return ResponseEntity.ok("Batch job '" + jobName + "' avviato con successo!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Errore nell'avvio del job '" + jobName + "': " + e.getMessage());
        }
    }
}