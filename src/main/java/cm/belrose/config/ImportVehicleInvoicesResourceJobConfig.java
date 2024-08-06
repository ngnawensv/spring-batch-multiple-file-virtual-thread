package cm.belrose.config;

import cm.belrose.client.DigitalStorageClient;
import cm.belrose.processor.InvoiceResourceItemProcessor;
import cm.belrose.config.properties.InputProperties;
import cm.belrose.dto.UploadFileDto;
import cm.belrose.dto.VehicleForJsonDto;
import cm.belrose.listener.CustomJobExecutionListener;
import cm.belrose.service.EmailService;
import cm.belrose.service.dto.EmailContent;
import cm.belrose.writer.DigitalStorageItemWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.ResourcesItemReader;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ImportVehicleInvoicesResourceJobConfig {

    @Value("${input.all}")
    private Resource[] resources;
    //private final InputProperties inputProperties;
    private final CustomJobExecutionListener customJobExecutionListener;
    private final InvoiceResourceItemProcessor invoiceResourceItemProcessor;
    private final DigitalStorageItemWriter digitalStorageItemWriter;
   // private final DigitalStorageClient  digitalStorageClient;
    private final EmailService emailService;

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
                .resources(resources)
                .delegate(jsonItemReader())
                .build();
    }

    /**
     * this method allows us to read multiple resources
     * the ResourcesItemReader is running in thread safe
     */
    public ResourcesItemReader resourcesItemReader(){
        var resourceItemReader = new ResourcesItemReader();
        resourceItemReader.setResources(resources);
        return resourceItemReader;
    }

    @Bean
    public Step importVehicleStep(final JobRepository jobRepository, final PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("import Invoices Step", jobRepository)
                .<Resource, UploadFileDto>chunk(2, platformTransactionManager)
                .reader(resourcesItemReader())
                .processor(invoiceResourceItemProcessor)
                .writer(digitalStorageItemWriter)
                .listener(promotionListener())
                //.writer(itemWriterAdapter())
                .taskExecutor(taskExecutor())
                .build();
    }

    /**
     * In this step we use tasklet
     */
    @Bean
    public Step mailSenderStep(final JobRepository jobRepository, final PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("mail send Step", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        //Here we are getting the data share between two steps using ExecutionContextPromotionListener
                        List<String> filesUploadedCodes = (List<String>) chunkContext.
                                getStepContext()
                                .getJobExecutionContext()
                                .get("filesUploadedCodes");
                        var emailContent = new EmailContent(filesUploadedCodes);
                        emailService.send(emailContent);
                        return RepeatStatus.FINISHED;
                    }
                },platformTransactionManager)
                .build();
    }

    @Bean
    public Job importVehicleJob(final JobRepository jobRepository, final Step importVehicleStep, final Step mailSenderStep) {
        return new JobBuilder("import Invoices Job" , jobRepository)
                .incrementer(new RunIdIncrementer()) //Use it if you want. each the job is executed it increment
                .start(importVehicleStep)
                .next(mailSenderStep)
                .listener(customJobExecutionListener)
                .build();
    }

    /**
     * Virtual Threads in java is design for simplified and scalable concurrent programing within the JVM
     */
    public VirtualThreadTaskExecutor taskExecutor(){
        return new VirtualThreadTaskExecutor("resource-Thread-");
    }

    /**
     * Promote items from the Step ExecutionContext to the Job ExecutionContext at the end of a step
     * In our case we share filesUploadedCodes
     */
    @Bean
    public ExecutionContextPromotionListener promotionListener(){
        var promotionListener = new ExecutionContextPromotionListener();
        promotionListener.setKeys(new String[]{"filesUploadedCodes"}); // filesUploadedCodes come from DigitalStorageItemWriter
        return promotionListener;
    }

    /**
     * this method is required when the ItemWriter or ItemReader is used to communicating with an external service
     * This method is not use now because our downstream api is not ready now
     */
   /* @Bean
    public ItemWriterAdapter<UploadFileDto> itemWriterAdapter(){
        var writerAdapter = new ItemWriterAdapter<UploadFileDto>();
        writerAdapter.setTargetObject(digitalStorageClient);
        writerAdapter.setTargetMethod("upload"); //name of the method inside the DigitalStorageClient
        return writerAdapter;
    }*/
}
