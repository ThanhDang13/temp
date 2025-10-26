package realm.packages.vertx.core.config.vertx.security;

import io.vertx.core.http.HttpMethod;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import realm.packages.vertx.core.config.vertx.security.model.UrlPatternMatcher;
import realm.packages.vertx.core.config.vertx.security.model.level.SecurityLevel;

public class SecurityResolver {

  private final List<Pair<UrlPatternMatcher.Request, SecurityLevel>> urlSecurityMapper =
      new ArrayList<>();

  public UrlPatternMatcher match(String pattern) {
    return new UrlPatternMatcher(this, pattern);
  }

  public UrlPatternMatcher match(String pattern, HttpMethod httpMethod) {
    return new UrlPatternMatcher(this, pattern, httpMethod);
  }

  public void put(UrlPatternMatcher.Request request, SecurityLevel securityLevel) {
    urlSecurityMapper.add(Pair.of(request, securityLevel));
  }

  public List<Pair<UrlPatternMatcher.Request, SecurityLevel>> getUrlSecurityMapper() {
    return urlSecurityMapper;
  }
}
