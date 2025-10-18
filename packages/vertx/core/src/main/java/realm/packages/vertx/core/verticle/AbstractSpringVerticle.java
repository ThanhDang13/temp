package realm.packages.vertx.core.verticle;

import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.http.HttpServer;
import io.vertx.rxjava3.ext.web.Router;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import realm.packages.vertx.core.config.vertx.binder.VertxBeforeCenterBinder;
import realm.packages.vertx.core.config.vertx.binder.VertxEBCenterBinder;
import realm.packages.vertx.core.config.vertx.binder.VertxOtherCenterBinder;
import realm.packages.vertx.core.config.vertx.binder.VertxRoutingCenterBinder;
import realm.packages.vertx.core.config.vertx.deploy.VertxDeployer;

@Getter
@Setter
public abstract class AbstractSpringVerticle extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected GenericApplicationContext applicationContext;
    public Router router;
    public HttpServer server;
    protected ConfigurableEnvironment env;

    @Override
    public void start() throws Exception {
        super.start();
        init();
        onStart();
        registerBeforeRouter();
        registerRouter();
        registerEB();
        registerOthers();

    }

    private void registerBeforeRouter() {
        VertxBeforeCenterBinder binder = applicationContext.getBean(VertxBeforeCenterBinder.class);
        binder.bind(this);
    }

    private void init() {
        // init ApplicationContext
        applicationContext = (GenericApplicationContext) VertxDeployer.getContext();
        env = applicationContext.getEnvironment();
    }

    private void registerRouter() throws Exception {
        VertxRoutingCenterBinder binder = applicationContext.getBean(VertxRoutingCenterBinder.class);
        binder.bind(router);
    }


    private void registerEB() {
        VertxEBCenterBinder binder = applicationContext.getBean(VertxEBCenterBinder.class);
        binder.bind(vertx.eventBus());
    }

    private void registerOthers() {
        VertxOtherCenterBinder binder = applicationContext.getBean(VertxOtherCenterBinder.class);
        binder.bind(this);
    }

    public abstract void onStart() throws Exception;
}
