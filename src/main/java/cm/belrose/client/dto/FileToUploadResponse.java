package cm.belrose.client.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileToUploadResponse{
    private String code;
    private String data;
}
