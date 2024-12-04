package cm.belrose;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @EnableScheduling in spring enables periodic task scheduling using @Scheduled annotations.
 * It's often used in Spring Batch to automate the execution of batch jobs at specified intervals
 */
@SpringBootApplication
@EnableScheduling
//@EnableConfigurationProperties(InputProperties.class)
public class SpringBatchMultipleFileVirtualThreadApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchMultipleFileVirtualThreadApplication.class, args);
	}

}
