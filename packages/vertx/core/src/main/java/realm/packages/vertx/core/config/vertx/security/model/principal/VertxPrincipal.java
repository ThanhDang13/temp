package realm.packages.vertx.core.config.vertx.security.model.principal;

public interface VertxPrincipal {

    public VertxAuthentication getAuthentication();
    default public Object otherInfo() {
        return null;
    }

    default public Object id() {
        return null;
    }
}
