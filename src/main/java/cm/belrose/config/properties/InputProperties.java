package cm.belrose.config.properties;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Dynamic configuration for application properties
 */
@Component
@ConfigurationProperties(prefix = "input")
@Data
@RequiredArgsConstructor
@Slf4j
public class InputProperties {

    private final Map<String, String> files;
    private final Map<String, String> jsons;
    private final Map<String, String> pdfs;

    public Resource[] getResources() {
        return getFiles().values().stream()
                .map(ClassPathResource::new)
                .toArray(Resource[]::new);
    }

    public Resource[] getJsonResources() {
        return getJsons().values().stream()
                .map(ClassPathResource::new)
                .toArray(Resource[]::new);
    }

    public Resource[] getPdfResources() {
        return getPdfs().values().stream()
                .map(ClassPathResource::new)
                .toArray(Resource[]::new);
    }
}

