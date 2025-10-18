package realm.packages.vertx.core.config.application;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter @Setter
@Configuration
@ConfigurationProperties("application")
public class ApplicationConfigModel {

    private String basePackage;
}
