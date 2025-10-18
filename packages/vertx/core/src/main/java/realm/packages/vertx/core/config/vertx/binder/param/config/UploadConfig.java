package realm.packages.vertx.core.config.vertx.binder.param.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("vertx.upload")
@Getter
@Setter
public class UploadConfig {
    String uploadDir = "tmp";
    Integer maxFileSize = 300;
}
