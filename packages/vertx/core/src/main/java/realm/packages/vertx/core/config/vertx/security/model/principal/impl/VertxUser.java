package realm.packages.vertx.core.config.vertx.security.model.principal.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.Authorization;
import lombok.Data;
import realm.packages.vertx.core.config.vertx.security.model.principal.VertxPrincipal;

@Data
public class VertxUser implements User {

  private VertxPrincipal principal;

  public VertxUser() {}

  public VertxUser(VertxPrincipal principal) {
    this.principal = principal;
  }

  @Override
  public JsonObject attributes() {
    return null;
  }

  @Override
  public User isAuthorized(Authorization authorization, Handler<AsyncResult<Boolean>> handler) {
    return null;
  }

  @Override
  public JsonObject principal() {
    return null;
  }

  @Override
  public void setAuthProvider(AuthProvider authProvider) {}

  @Override
  public User merge(User user) {
    return null;
  }
}
