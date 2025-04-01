package it.batch.controller;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
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
    private Job copyBookJob; // Nome del job da eseguire

    @PostMapping("/start-copy-job")
    public ResponseEntity<String> startCopyBookJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis()) // Per garantire unicità
                    .toJobParameters();

            jobLauncher.run(copyBookJob, jobParameters);
            return ResponseEntity.ok("Batch job copyBookJob avviato con successo!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Errore nell'avvio del job: " + e.getMessage());
        }
    }
}