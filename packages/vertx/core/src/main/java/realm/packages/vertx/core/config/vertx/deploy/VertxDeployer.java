package realm.packages.vertx.core.config.vertx.deploy;

import io.reactivex.rxjava3.core.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.rxjava3.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import realm.packages.vertx.core.config.vertx.VertxWrapper;
import realm.packages.vertx.core.config.vertx.deploy.event.VerticleUpEvent;
import realm.packages.vertx.core.config.vertx.deploy.model.DeploymentOptionModel;
import realm.packages.vertx.core.config.vertx.deploy.model.VertxOptionsModel;

import java.io.IOException;

@Configuration
public class VertxDeployer {

    private static ApplicationContext staticApplicationContext;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired private Environment env;
    @Autowired private ApplicationContext applicationContext;
    @Autowired private DeploymentOptionModel deploymentOptionModel;
    @Autowired private VertxOptionsModel vertxOptionsModel;

    public void deployVerticle(String verticleClass) throws IOException {
        setContext(applicationContext);

        Single.just(env.getProperty("vertx.clustered.enabled", Boolean.class, false))
                .doOnSuccess(cluster -> deployStandardVerticle(cluster, verticleClass).subscribe())
                .subscribe(cluster -> {
                    if (cluster) {
                        logger.debug("Cluster is enabled");
                    }
                });
    }

    private Single<String> deployStandardVerticle(Boolean cluster, String verticleClass) {
        DeploymentOptions opt = deploymentOptionModel.toDeploymentOptions();
        if (cluster) {
            return deployCluster(vertxOptionsModel.toVertxOptions(), verticleClass, opt);
        } else {
            return deployOnVertx(VertxWrapper.vertx(), verticleClass, opt);
        }
    }

    private Single<String> deployCluster(VertxOptions vertxOptions, String className, DeploymentOptions deploymentOptions) {
        return Vertx.rxClusteredVertx(vertxOptions)
                .flatMap(clusteredVertx -> {
                    VertxWrapper.refreshVertxBean(clusteredVertx);
                    return deployOnVertx(clusteredVertx, className, deploymentOptions);
                });
    }

    private Single<String> deployOnVertx(Vertx vertx, String className, DeploymentOptions opt) {
        return vertx.rxDeployVerticle(className, opt)
                .doOnSuccess(id -> {
                    logger.info("Verticle {} is deployed in {} mode with \n id {}  \n and deployment options: {} \n {}",
                            className,
                            vertx.isClustered() ? "cluster" : "non cluster",
                            id,
                            opt.toJson().toString(),
                            vertx.isClustered() ? "cluster options: " + vertxOptionsModel.toVertxOptions().toString() : "");

                    // Publish event Verticle is up if success
                    applicationContext.publishEvent(new VerticleUpEvent(this));
                });
    }

    private static void setContext(ApplicationContext applicationContext) {
        if (staticApplicationContext == null) {
            staticApplicationContext = applicationContext;
        }
    }

    public static ApplicationContext getContext() {
        return staticApplicationContext;
    }
}
