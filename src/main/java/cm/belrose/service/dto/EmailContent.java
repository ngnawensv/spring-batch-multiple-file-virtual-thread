package cm.belrose.service.dto;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
public record EmailContent(List<String> fileCodes) {
    public EmailContent {
        Objects.requireNonNull(fileCodes,"File codes cannot be null");
        if(fileCodes.isEmpty()){
           log.warn("No content for email");
        }
    }
}
