package realm.packages.vertx.core.config.vertx.exeception;

public class VertxSpringCoreWaningException extends VertxSpringCoreException {

    public VertxSpringCoreWaningException(String message) {
        super(message);
    }

    public VertxSpringCoreWaningException(String message, int statusCode) {
        super(message, statusCode);
    }
}
