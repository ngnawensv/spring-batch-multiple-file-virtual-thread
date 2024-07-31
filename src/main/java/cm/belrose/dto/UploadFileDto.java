package cm.belrose.dto;

import java.io.File;

public record UploadFileDto(String fileName,
                            File file) { }
