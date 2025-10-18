package realm.packages.vertx.core.config.vertx.deploy.event;

import org.springframework.context.ApplicationEvent;

public class VerticleUpEvent extends ApplicationEvent {

    public VerticleUpEvent(Object source) {
        super(source);
    }
}
