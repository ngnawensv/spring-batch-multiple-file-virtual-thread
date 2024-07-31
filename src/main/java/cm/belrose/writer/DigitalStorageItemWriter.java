package cm.belrose.writer;

import cm.belrose.client.DigitalStorageClient;
import cm.belrose.dto.UploadFileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DigitalStorageItemWriter implements ItemWriter<UploadFileDto> {

     public final DigitalStorageClient digitalStorageClient;

    @Override
    public void write(Chunk<? extends UploadFileDto> chunk) throws Exception {
        log.info("Writing items: {}", chunk);
        chunk.forEach(this::upload);
    }

    private void upload(UploadFileDto uploadFileDto) {
        //String fileCode = digitalStorageClient.upload(item.file());
        log.info("--------->File successful upload");
    }
}
