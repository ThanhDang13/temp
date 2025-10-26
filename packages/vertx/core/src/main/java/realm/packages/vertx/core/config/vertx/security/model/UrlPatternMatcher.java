package realm.packages.vertx.core.config.vertx.security.model;

import io.vertx.core.http.HttpMethod;
import lombok.Data;
import realm.packages.vertx.core.config.vertx.security.SecurityResolver;
import realm.packages.vertx.core.config.vertx.security.model.level.Authenticated;
import realm.packages.vertx.core.config.vertx.security.model.level.Free;
import realm.packages.vertx.core.config.vertx.security.model.level.HasAuthority;

@Data
public class UrlPatternMatcher {

  private SecurityResolver securityResolver;
  private Request request;

  public UrlPatternMatcher(SecurityResolver securityResolver, String urlPatter) {
    this.securityResolver = securityResolver;
    this.request = Request.from(urlPatter);
  }

  public UrlPatternMatcher(
      SecurityResolver securityResolver, String urlPatter, HttpMethod httpMethod) {
    this(securityResolver, urlPatter);
    this.request = Request.from(urlPatter, httpMethod);
  }

  public SecurityResolver isAuthenticated() {
    securityResolver.put(request, Authenticated.authenticated());
    return securityResolver;
  }

  public SecurityResolver free() {
    securityResolver.put(request, Free.free());
    return securityResolver;
  }

  public SecurityResolver hasRole(String role) {
    securityResolver.put(request, new HasAuthority(role));
    return securityResolver;
  }

  public SecurityResolver hasAnyRoles(String... roles) {
    securityResolver.put(request, new HasAuthority(roles));
    return securityResolver;
  }

  @Data
  public static class Request {
    private String urlPatter;
    private HttpMethod httpMethod;

    private Request(String urlPatter, HttpMethod httpMethod) {
      this.urlPatter = urlPatter;
      this.httpMethod = httpMethod;
    }

    private Request(String urlPatter) {
      this.urlPatter = urlPatter;
    }

    public static Request from(String urlPatter, HttpMethod httpMethod) {
      return new Request(urlPatter, httpMethod);
    }

    public static Request from(String urlPatter) {
      return new Request(urlPatter);
    }
  }
}
