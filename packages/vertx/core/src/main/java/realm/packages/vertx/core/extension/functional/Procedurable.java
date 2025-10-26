package realm.packages.vertx.core.extension.functional;

import realm.packages.vertx.core.config.vertx.exception.VertxSpringCoreException;

@FunctionalInterface
public interface Procedurable {

  public void process() throws VertxSpringCoreException;
}
