package cm.belrose.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

/**
 * JobExecutionListener is the interface helping to customize behavior before and after executing the batch of job
 */
@Component
@Slf4j
public class CustomJobExecutionListener implements JobExecutionListener {

    //here we are going to count how many line we are read of the job execution
    @Override
    public void afterJob(JobExecution jobExecution) {
        jobExecution.getStepExecutions()
                .stream()
                .findFirst()
                .ifPresent(stepExecution -> {
                    long writeCount = stepExecution.getWriteCount();
                    log.info(String.format("CustomJobExecutionListener:afterJob::The job has written %s lines", writeCount));
                });
    }
}
