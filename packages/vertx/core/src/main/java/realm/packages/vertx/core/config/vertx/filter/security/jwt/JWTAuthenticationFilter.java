package realm.packages.vertx.core.config.vertx.filter.security.jwt;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.rxjava3.core.http.HttpServerRequest;
import io.vertx.rxjava3.ext.web.RoutingContext;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import realm.packages.vertx.core.config.vertx.filter.VertxGenericFilter;
import realm.packages.vertx.core.config.vertx.security.model.UrlPatternMatcher;
import realm.packages.vertx.core.config.vertx.security.model.principal.VertxPrincipal;
import realm.packages.vertx.core.config.vertx.security.model.principal.impl.VertxUser;

@ConditionalOnProperty("vertx.security.jwt.enabled")
// @Component
public class JWTAuthenticationFilter implements VertxGenericFilter {

  @Autowired private JWTService jwtService;
  @Autowired private Environment env;

  private List<String> mappingUrls;

  @PostConstruct
  public void init() {
    String mappingUrlsString = env.getProperty("vertx.security.jwt.mapping-urls");
    if (mappingUrlsString != null) {
      mappingUrls =
          Stream.of(mappingUrlsString.split(","))
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
    return mappingUrls.stream().map(UrlPatternMatcher.Request::from).collect(toList());
  }

  @Override
  public Completable doFilterRx(RoutingContext routingContext) {
    HttpServerRequest req = routingContext.request();
    String bearer = req.getHeader("Authorization");
    VertxPrincipal principal;
    String accessToken = null;
    if (bearer != null && bearer.startsWith("Bearer ")) {
      accessToken = bearer.substring("Bearer ".length());
    }
    principal = jwtService.extractPrincipal(accessToken, req);
    if (principal != null) {
      VertxUser user = new VertxUser(principal);
      routingContext.setUser(new io.vertx.rxjava3.ext.auth.User(user));
    }
    return Completable.complete();
  }
}
