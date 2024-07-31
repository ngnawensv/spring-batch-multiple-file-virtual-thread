package cm.belrose.config.processor;

import cm.belrose.dto.UploadFileDto;
import cm.belrose.processor.InvoiceResourceItemProcessor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.PathResource;

import java.nio.file.Path;

@SpringBootTest
class InvoiceResourceItemProcessorTest {

    @InjectMocks
    InvoiceResourceItemProcessor invoiceResourceItemProcessor;

    @Test
    @SneakyThrows
    void shouldProcess(){
        //given this resource
        Path path = Path.of("/data/resource/invoices.pdf");
        PathResource resource = new PathResource(path);

        //when process
        //var itemProcessor = new InvoiceResourceItemProcessor();
        UploadFileDto uploadFileDto = invoiceResourceItemProcessor.process(resource);

        //then
        assert uploadFileDto != null;
        Assertions.assertNotNull(uploadFileDto.file());
        Assertions.assertNotNull(uploadFileDto.fileName(), path.toFile().getName());
    }

}