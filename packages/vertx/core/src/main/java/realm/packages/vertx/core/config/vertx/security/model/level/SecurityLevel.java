package realm.packages.vertx.core.config.vertx.security.model.level;

import io.vertx.rxjava3.ext.auth.User;

public interface SecurityLevel {
  public static final String AUTHENTICATION = "vertx_spring_authentication";
  public static final boolean AUTHENTICATED = true;
  public static final boolean UN_AUTHENTICATED = false;

  boolean isPermitted(User user);
}
