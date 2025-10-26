package realm.packages.vertx.core.config.vertx.security.model.level;

import io.vertx.rxjava3.ext.auth.User;

public class Free implements SecurityLevel {

  private static final Free free = new Free();

  @Override
  public boolean isPermitted(User user) {
    return true;
  }

  public static Free free() {
    return free;
  }
}
