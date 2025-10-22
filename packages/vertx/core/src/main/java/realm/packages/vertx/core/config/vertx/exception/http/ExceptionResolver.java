package realm.packages.vertx.core.config.vertx.exception.http;

import io.vertx.rxjava3.ext.web.RoutingContext;

public interface ExceptionResolver {
    public void resolveException(RoutingContext context, Throwable throwable);
}
