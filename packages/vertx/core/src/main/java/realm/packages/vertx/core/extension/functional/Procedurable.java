package realm.packages.vertx.core.extension.functional;

import realm.packages.vertx.core.config.vertx.exeception.VertxSpringCoreException;

@FunctionalInterface
public interface Procedurable {

    public void process() throws VertxSpringCoreException;
}
