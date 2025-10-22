package realm.apps.api;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import realm.packages.vertx.core.annotation.EnableAutoDeployVerticle;

@ComponentScan({"realm.apps.api", "realm.packages.vertx.core"})
//@EnableAutoDeployVerticle
@SpringBootApplication
public class RealmApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(RealmApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
