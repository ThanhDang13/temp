package realm.packages.vertx.core.config.vertx.handler.http;

public interface ControllerResultResolver {

    public void resolve(ControllerIOWrapper ioWrapper) throws Exception;
}
