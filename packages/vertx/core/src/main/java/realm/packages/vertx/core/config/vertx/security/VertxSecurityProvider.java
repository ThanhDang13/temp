package realm.packages.vertx.core.config.vertx.security;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import realm.packages.vertx.core.config.vertx.filter.VertxGenericFilter;
import realm.packages.vertx.core.config.vertx.filter.security.BackAuthenticationExtractingFilter;
import realm.packages.vertx.core.config.vertx.filter.security.FrontAuthenticationExtractingFilter;
import realm.packages.vertx.core.config.vertx.security.model.UrlPatternMatcher;
import realm.packages.vertx.core.config.vertx.security.model.level.SecurityLevel;

@Component
public class VertxSecurityProvider {

  @Autowired private Environment env;

  private boolean isEnableSecurity() {
    return env.getProperty("vertx.security.enabled", Boolean.class, false);
  }

  public List<VertxGenericFilter> generateSecurityFilters(SecurityResolver securityResolver) {
    if (isEnableSecurity()) {
      List<Pair<UrlPatternMatcher.Request, SecurityLevel>> mapper =
          securityResolver.getUrlSecurityMapper();

      // Front filters matching cofigured request patterns, checking them is permitted or not
      List<VertxGenericFilter> filters =
          mapper.stream()
              .map(pair -> new FrontAuthenticationExtractingFilter(pair.getLeft(), pair.getRight()))
              .collect(toList());

      // Back filter match all request, and decided should request goes to the controller or not
      BackAuthenticationExtractingFilter backFilter = new BackAuthenticationExtractingFilter();
      filters.add(backFilter);

      return filters;
    }

    return Collections.emptyList();
  }
}
