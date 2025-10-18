package realm.packages.vertx.core.config.vertx.binder.before;

import realm.packages.vertx.core.verticle.AbstractSpringVerticle;

public interface VertxBeforeRouterBinder {
    public void bind(AbstractSpringVerticle verticle);
}
