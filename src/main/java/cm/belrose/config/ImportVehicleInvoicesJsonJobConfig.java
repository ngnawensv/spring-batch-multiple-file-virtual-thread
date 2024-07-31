package cm.belrose.config;

import cm.belrose.config.properties.InputProperties;
import cm.belrose.dto.VehicleForJsonDto;
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
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemReader;
import org.springframework.batch.item.support.builder.SynchronizedItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ImportVehicleInvoicesJsonJobConfig {

    private final InputProperties inputProperties;
    private final CustomJobExecutionListener customJobExecutionListener;

    /**
     * ResourceAwareItemReaderItemStream is an interface, and we can use it instead FlatFileItemReader directly
     * It is a good practice
     */
    @Bean
    public JsonItemReader<VehicleForJsonDto> jsonItemReader() {
        return new JsonItemReaderBuilder<VehicleForJsonDto>()
                .name("Vehicle item reader")
                .jsonObjectReader(new JacksonJsonObjectReader<>(VehicleForJsonDto.class))
                .strict(false)
                .build();
    }

    /**
     * This method is used for reading multiple resource
     * the jsonItemReader is delegate to this method (multiResourceItemReader)
     */
    public MultiResourceItemReader<VehicleForJsonDto> multiResourceItemReader(){
        return new MultiResourceItemReaderBuilder<VehicleForJsonDto>()
                .name("Vehicle resources reader")
                .resources(inputProperties.getJsonResources())
                .delegate(jsonItemReader())
                .build();
    }

    /**
     *this method is the custom multi thread safe.
     */
    /*public MultiResourceReaderThreadSafe<VehicleForJsonDto> multiResourceReaderThreadSafe(){
        var multiResourceReader = new MultiResourceReaderThreadSafe<>(multiResourceItemReader());
        multiResourceReader.setResources(inputProperties.getJsonResources());
        return multiResourceReader;
    }*/

    /**
     *When using multiResource, we have to delegate multiResourceItemReader to this
     * synchronizedItemReader methode
     */
    public SynchronizedItemReader<VehicleForJsonDto> synchronizedItemReader(){
        return new SynchronizedItemReaderBuilder<VehicleForJsonDto>()
                .delegate(multiResourceItemReader())
                .build();
    }


    @Bean
    public Step importVehicleStep(final JobRepository jobRepository, final PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("importVehicleStep", jobRepository)
                .<VehicleForJsonDto, VehicleForJsonDto>chunk(100, platformTransactionManager)
                .reader(synchronizedItemReader())
                .processor(this::vehicleProcessor)
                .writer(items->log.info("Writing items: {}", items))
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Job importVehicleJob(final JobRepository jobRepository, final Step importVehicleStep) {
        return new JobBuilder("importVehicleJob" , jobRepository)
                .incrementer(new RunIdIncrementer()) //Use it if you want. each the job is executed it increment
                .start(importVehicleStep)
                .listener(customJobExecutionListener)
                .build();
    }

    /**
     * Virtual Threads in java is design for simplified and scalable concurrent programing within the JVM
     */
    public VirtualThreadTaskExecutor taskExecutor(){
        return new VirtualThreadTaskExecutor("Json-Thread-");
    }

    private VehicleForJsonDto vehicleProcessor(VehicleForJsonDto item) {
        log.info("Processing the item: {}", item);
        return item;
    }


}
