package realm.packages.vertx.core.config.vertx.filter.security;

import static java.util.Arrays.asList;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.rxjava3.ext.web.RoutingContext;
import java.util.List;
import lombok.Data;
import realm.packages.vertx.core.config.vertx.filter.VertxGenericFilter;
import realm.packages.vertx.core.config.vertx.security.model.UrlPatternMatcher;
import realm.packages.vertx.core.config.vertx.security.model.level.SecurityLevel;

@Data
public class FrontAuthenticationExtractingFilter implements VertxGenericFilter {

  private SecurityLevel securityLevel;
  private UrlPatternMatcher.Request request;

  public FrontAuthenticationExtractingFilter(
      UrlPatternMatcher.Request request, SecurityLevel securityLevel) {
    this.securityLevel = securityLevel;
    this.request = request;
  }

  @Override
  public List<UrlPatternMatcher.Request> mappingUrls() {
    return asList(request);
  }

  @Override
  public Completable doFilterRx(RoutingContext routingContext) {
    if (securityLevel.isPermitted(routingContext.user())) {
      routingContext.put(SecurityLevel.AUTHENTICATION, SecurityLevel.AUTHENTICATED);
    } else {
      routingContext.put(SecurityLevel.AUTHENTICATION, SecurityLevel.UN_AUTHENTICATED);
    }

    return Completable.complete();
  }
}
