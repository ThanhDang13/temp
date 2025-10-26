package realm.packages.vertx.core.config.vertx.security.model.principal.impl;

import java.util.List;
import realm.packages.vertx.core.config.vertx.security.model.principal.VertxAuthentication;

public class SimpleAuthentication implements VertxAuthentication {

  private List<String> authorities;

  public SimpleAuthentication(List<String> authorities) {
    this.authorities = authorities;
  }

  @Override
  public List<String> getAuthorities() {
    return authorities;
  }
}
