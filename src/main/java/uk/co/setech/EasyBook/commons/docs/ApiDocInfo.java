package uk.co.setech.EasyBook.commons.docs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "api.info")
public class ApiDocInfo {
    private String title;
    private String description;
    private String version;
}
