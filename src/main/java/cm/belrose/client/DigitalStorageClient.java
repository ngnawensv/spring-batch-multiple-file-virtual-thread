package cm.belrose.client;

import cm.belrose.client.dto.FileToUpload;
import cm.belrose.client.dto.FileToUploadResponse;
import cm.belrose.dto.UploadFileDto;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.file.Files;

@Component
public class DigitalStorageClient {

    private final RestClient restClient = RestClient.create();

    @SneakyThrows
    public  FileToUploadResponse upload(UploadFileDto uploadFileDto){
        var fileToUpload = new FileToUpload(uploadFileDto.fileName(), Files.readAllBytes(uploadFileDto.file().toPath()));
        return restClient.post()
                .uri("http://localhost:8080/storage")
                .body(fileToUpload)
                .retrieve()
                .body(FileToUploadResponse.class);
    }
}
