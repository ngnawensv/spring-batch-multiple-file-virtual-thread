package cm.belrose.config.processor;
/**
 * This test is more easy
 */

import cm.belrose.dto.UploadFileDto;
import cm.belrose.processor.InvoiceResourceItemProcessor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.PathResource;

import java.nio.file.Path;

class InvoiceResourceItemProcessor1Test {

    @Test
    @SneakyThrows
    void shouldProcess(){
        //given this resource
        Path path = Path.of("/data/resource/invoices.pdf");
        PathResource resource = new PathResource(path);

        //when process
        var itemProcessor = new InvoiceResourceItemProcessor();
        UploadFileDto uploadFileDto = itemProcessor.process(resource);

        //then
        assert uploadFileDto != null;
        Assertions.assertNotNull(uploadFileDto.file());
        Assertions.assertNotNull(uploadFileDto.fileName(), path.toFile().getName());
    }

}