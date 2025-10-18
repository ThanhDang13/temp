package realm.packages.vertx.core.config.vertx.filter;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.rxjava3.ext.web.RoutingContext;
import realm.packages.vertx.core.config.vertx.security.model.UrlPatternMatcher;

import java.util.List;

public interface VertxGenericFilter {

    public List<UrlPatternMatcher.Request> mappingUrls();

    public Completable doFilterRx(RoutingContext routingContext);

    public default Completable doFilter(io.vertx.ext.web.RoutingContext routingContext) {
        return doFilterRx(new RoutingContext(routingContext));
    }

    public default int order() {
        return 0;
    }
}
