package realm.packages.vertx.core.config.vertx.filter.security;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.rxjava3.ext.web.RoutingContext;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import realm.packages.vertx.core.config.vertx.filter.VertxGenericFilter;
import realm.packages.vertx.core.config.vertx.security.model.UrlPatternMatcher;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;
import static realm.packages.vertx.core.template.VertxExecution.rxBlockingAsync;

public abstract class DefaultJWTAuthenticationFilter implements VertxGenericFilter {

    @Autowired
    protected Environment env;

    protected List<String> mappingUrls;

    @PostConstruct
    public void init() {
        String mappingUrlsString = env.getProperty("vertx.security.jwt.mapping-urls");
        if (mappingUrlsString != null) {
            mappingUrls = Stream.of(mappingUrlsString.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(toList());
        }

        if (isEmpty(mappingUrls)) {
            mappingUrls = asList("/*");
        }
    }

    @Override
    public List<UrlPatternMatcher.Request> mappingUrls() {
        return mappingUrls.stream()
                .map(UrlPatternMatcher.Request::from)
                .collect(toList());
    }

    @Override
    public Completable doFilterRx(RoutingContext routingContext) {
        routingContext.request().pause();
        return rxBlockingAsync(() -> doFilterRxBlocking(routingContext), true).ignoreElement(); // tương đương toCompletable()
    }

    protected Completable doFilterRxBlocking(RoutingContext routingContext) {
        return Completable.complete();
    }
}
