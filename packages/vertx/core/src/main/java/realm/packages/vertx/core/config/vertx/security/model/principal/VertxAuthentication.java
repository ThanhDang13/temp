package realm.packages.vertx.core.config.vertx.security.model.principal;

import java.util.List;

public interface VertxAuthentication {

  List<String> getAuthorities();
}
