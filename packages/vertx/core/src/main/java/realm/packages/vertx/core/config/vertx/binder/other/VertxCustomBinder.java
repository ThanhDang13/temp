package realm.packages.vertx.core.config.vertx.binder.other;

import realm.packages.vertx.core.verticle.AbstractSpringVerticle;

public interface VertxCustomBinder {
    public void bind(AbstractSpringVerticle verticle);
}
