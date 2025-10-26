package realm.packages.vertx.core.config.vertx;

import io.vertx.rxjava3.core.Vertx;

public class VertxWrapper {

  private static Vertx VERTX = Vertx.vertx();

  public static Vertx vertx() {
    return VERTX;
  }

  public static void refreshVertxBean(Vertx vertx) {
    VERTX = vertx;
  }
}
