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
public class JobVehicleInvoicesResourceTrigger {

    private final JobLauncher jobLauncher;
    private final Job importVehicleInvoicesJob;


    @Autowired
    public JobVehicleInvoicesResourceTrigger(JobLauncher jobLauncher, @Qualifier("importVehicleInvoicesJob") Job importVehicleInvoicesJob) {
        this.jobLauncher = jobLauncher;
        this.importVehicleInvoicesJob = importVehicleInvoicesJob;
    }

    @Scheduled(cron = "0/30 * * ? * *")
    @SneakyThrows
    void  launchJobPeriodically1(){
        log.info("==============>launching the job");
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("importVehicleInvoicesJob", "importVehicleInvoicesJob")
                .toJobParameters();
        JobExecution jobExecution = this.jobLauncher.run(importVehicleInvoicesJob,jobParameters);
        log.info("Job finished with the status: {}", jobExecution.getExitStatus());
    }
}
