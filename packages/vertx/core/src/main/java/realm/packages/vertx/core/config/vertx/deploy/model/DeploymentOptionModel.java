package realm.packages.vertx.core.config.vertx.deploy.model;

import io.vertx.core.DeploymentOptions;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("vertx.deployment-options")
public class DeploymentOptionModel {

    protected Boolean worker;
    protected Boolean multiThreaded;
    protected String isolationGroup;
    protected String workerPoolName;
    protected Integer workerPoolSize;
    protected Long maxWorkerExecuteTime;
    protected Boolean ha;
    protected Integer instances;

    public DeploymentOptions toDeploymentOptions() {
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        if (worker != null) deploymentOptions.setWorker(worker);
//        if (multiThreaded != null) deploymentOptions.setMultiThreaded(multiThreaded);
        if (workerPoolName != null) deploymentOptions.setWorkerPoolName(workerPoolName);
        if (workerPoolSize != null) deploymentOptions.setWorkerPoolSize(workerPoolSize);
        if (maxWorkerExecuteTime != null) deploymentOptions.setMaxWorkerExecuteTime(maxWorkerExecuteTime);
        if (ha != null) deploymentOptions.setHa(ha);
        if (instances != null) deploymentOptions.setInstances(instances);

        return deploymentOptions;
    }
}
