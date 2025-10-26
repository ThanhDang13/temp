package realm.packages.vertx.core.config.vertx.binder;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import realm.packages.vertx.core.config.vertx.binder.other.VertxCustomBinder;
import realm.packages.vertx.core.verticle.AbstractSpringVerticle;

@Component
public class VertxOtherCenterBinder {

  @Autowired private ApplicationContext context;

  public void bind(AbstractSpringVerticle verticle) {
    Collection<VertxCustomBinder> customBinders =
        context.getBeansOfType(VertxCustomBinder.class).values();
    customBinders.forEach(binder -> binder.bind(verticle));
  }
}
