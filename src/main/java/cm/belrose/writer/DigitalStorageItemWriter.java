package cm.belrose.writer;

import cm.belrose.client.DigitalStorageClient;
import cm.belrose.client.dto.FileToUploadResponse;
import cm.belrose.dto.UploadFileDto;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DigitalStorageItemWriter implements ItemWriter<UploadFileDto> {

     public final DigitalStorageClient digitalStorageClient;
     //StepExecution represent all what happening in our Step
     private StepExecution stepExecution;

    @Override
    public void write(@Nonnull Chunk<? extends UploadFileDto> chunk) throws Exception {
        log.info("Writing items: {}", chunk);
        chunk.forEach(this::process);
    }

    private void process(UploadFileDto item) {
       List<String> filesUploadedCodes = (List<String>) stepExecution.getExecutionContext().get("filesUploadedCodes");
        FileToUploadResponse filesUploadedResponse = upload(item);
        filesUploadedCodes.add(filesUploadedResponse.getCode());
    }

    private FileToUploadResponse upload(UploadFileDto uploadFileDto) {
        //String fileCode = digitalStorageClient.upload(item.file());
        String fileCodeFake = UUID.randomUUID().toString(); // simulating the response of downstream call
        log.info("--------->File successful upload");
        return FileToUploadResponse.builder().code(fileCodeFake).data(null).build();
    }

    /**
     * initializing the step execution and filesUploadedCodes arrayList
     */
    @BeforeStep
    public void initialize(StepExecution stepExecution){
        this.stepExecution = stepExecution;
        this.stepExecution.getExecutionContext().put("filesUploadedCodes", new ArrayList<String>());
    }
}
