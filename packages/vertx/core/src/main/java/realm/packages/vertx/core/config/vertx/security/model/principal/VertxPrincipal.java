package realm.packages.vertx.core.config.vertx.security.model.principal;

public interface VertxPrincipal {

  public VertxAuthentication getAuthentication();

  public default Object otherInfo() {
    return null;
  }

  public default Object id() {
    return null;
  }
}
