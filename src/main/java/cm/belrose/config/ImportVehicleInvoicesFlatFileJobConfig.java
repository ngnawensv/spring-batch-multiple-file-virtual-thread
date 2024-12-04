/*

package cm.belrose.config;

import cm.belrose.config.properties.InputProperties;
import cm.belrose.dto.VehicleDto;
import cm.belrose.listener.CustomJobExecutionListener;
import cm.belrose.reader.MultiResourceReaderThreadSafe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ImportVehicleInvoicesFlatFileJobConfig {

    private final InputProperties inputFilesProperties;
    private final CustomJobExecutionListener customJobExecutionListener;


    */
/**
     * ResourceAwareItemReaderItemStream is an interface, and we can use it instead FlatFileItemReader directly
     * It is a good practice
     *//*


    public ResourceAwareItemReaderItemStream<VehicleDto> vehicleDtoFlatFileItemReader() {
        return new FlatFileItemReaderBuilder<VehicleDto>()
                .name("Vehicle item reader")
                .saveState(Boolean.FALSE)
                .linesToSkip(1) //skip the first line
                .delimited()
                .delimiter(",")
                .names("referenceNumber", "model", "type", "customerFullName")
                .comments("#") // specify to sky the comment line within the csv file
                .targetType(VehicleDto.class)
                .build();
    }


    */
/**
     * This method is used for reading multiple resource
     *//*


    public MultiResourceItemReader<VehicleDto> multiResourceItemReader() {
        return new MultiResourceItemReaderBuilder<VehicleDto>()
                .name("Vehicle resources reader")
                .resources(inputFilesProperties.getResources())
                .delegate(vehicleDtoFlatFileItemReader())
                .build();
    }


    */
/**
     * this method is for concurrency issue
     *//*


    public MultiResourceReaderThreadSafe<VehicleDto> multiResourceReaderThreadSafe() {
        var multiResourceReader = new MultiResourceReaderThreadSafe<>(multiResourceItemReader());
        multiResourceReader.setResources(inputFilesProperties.getResources());
        return multiResourceReader;
    }


    @Bean
    public Step importVehicleStep(final JobRepository jobRepository, final PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("importVehicleStep", jobRepository)
                .<VehicleDto, VehicleDto>chunk(100, platformTransactionManager)
                .reader(multiResourceReaderThreadSafe())
                .processor(this::vehicleProcessor)
                .writer(items -> log.info("Writing items: {}", items))
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Job importVehicleJob(final JobRepository jobRepository, final Step importVehicleStep) {
        return new JobBuilder("importVehicleJob", jobRepository)
                .incrementer(new RunIdIncrementer()) //Use it if you want. each the job is executed it increment
                .start(importVehicleStep)
                .listener(customJobExecutionListener)
                .build();
    }


    */
/**
     * Virtual Threads in java is design for simplified and scalable concurrent programing within the JVM
     *//*


    public VirtualThreadTaskExecutor taskExecutor() {
        return new VirtualThreadTaskExecutor("Custom-Thread-");
    }

    private VehicleDto vehicleProcessor(VehicleDto item) {
        log.info("Processing the item: {}", item);
        return item;
    }


}

*/
