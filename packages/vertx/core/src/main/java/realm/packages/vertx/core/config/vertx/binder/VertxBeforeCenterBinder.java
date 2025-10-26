package realm.packages.vertx.core.config.vertx.binder;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import realm.packages.vertx.core.config.vertx.binder.before.VertxBeforeRouterBinder;
import realm.packages.vertx.core.verticle.AbstractSpringVerticle;

@Component
public class VertxBeforeCenterBinder {

  @Autowired private ApplicationContext context;

  public void bind(AbstractSpringVerticle verticle) {
    Collection<VertxBeforeRouterBinder> customBinders =
        context.getBeansOfType(VertxBeforeRouterBinder.class).values();
    customBinders.forEach(binder -> binder.bind(verticle));
  }
}
