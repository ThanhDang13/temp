package realm.apps.api;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"realm.apps.api", "realm.packages.vertx.core"})
@SpringBootApplication
public class RealmApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(RealmApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
