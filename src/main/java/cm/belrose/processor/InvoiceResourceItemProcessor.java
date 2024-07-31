package cm.belrose.processor;

import cm.belrose.dto.UploadFileDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InvoiceResourceItemProcessor implements ItemProcessor<Resource, UploadFileDto> {
    @Override
    public UploadFileDto process(Resource item) throws Exception {
        log.info("=============>Processing the : {}",item);
        return new UploadFileDto(item.getFilename(), item.getFile());
    }
}
