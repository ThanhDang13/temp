package realm.packages.vertx.core.config.vertx.deploy;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import realm.packages.vertx.core.annotation.EnableAutoDeployVerticle;
import realm.packages.vertx.core.config.application.ApplicationConfigModel;

import java.util.ArrayList;
import java.util.Set;

@Component
public class VertxAutoDeployer {

    @Autowired private ApplicationConfigModel applicationConfigModel;
    @Autowired private VertxDeployer vertxDeployer;

    @PostConstruct
    public void searchForDeployment() {

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(EnableAutoDeployVerticle.class));

        Set<BeanDefinition> annotatedComponents = scanner.findCandidateComponents(applicationConfigModel.getBasePackage());
        ArrayList<BeanDefinition> list = new ArrayList<>(annotatedComponents);

        // auto deploy verticle
        if (!list.isEmpty()) {
            try {
                EnableAutoDeployVerticle autoDeploy = Class.forName(list.get(0).getBeanClassName()).getAnnotation(EnableAutoDeployVerticle.class);
                vertxDeployer.deployVerticle(autoDeploy.verticle().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
