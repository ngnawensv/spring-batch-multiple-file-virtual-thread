package cm.belrose.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JobVehicleInvoicesJsonTrigger {

    private final JobLauncher jobLauncher;
    private final Job importVehicleJsonJob;


    @Autowired
    public JobVehicleInvoicesJsonTrigger(JobLauncher jobLauncher,@Qualifier("importVehicleJsonJob") Job importVehicleJsonJob) {
        this.jobLauncher = jobLauncher;
        this.importVehicleJsonJob = importVehicleJsonJob;
    }

    @Scheduled(cron = "0/40 * * ? * *")
    @SneakyThrows
    void  launchJobPeriodically2(){
        log.info("==============>launching the job");
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("importVehicleJsonJob", "importVehicleJsonJob")
                .toJobParameters();
        JobExecution jobExecution = this.jobLauncher.run(importVehicleJsonJob,jobParameters);
        log.info("Job finished with the status: {}", jobExecution.getExitStatus());
    }
}
