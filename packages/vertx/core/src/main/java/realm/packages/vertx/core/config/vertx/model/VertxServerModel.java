package realm.packages.vertx.core.config.vertx.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("vertx.server")
public class VertxServerModel {

    private Integer port;
}
