package realm.packages.vertx.core.config.vertx.security.model.level;

import io.vertx.rxjava3.ext.auth.User;
import realm.packages.vertx.core.config.vertx.security.model.principal.impl.VertxUser;

public class Authenticated implements SecurityLevel {

    private final static Authenticated authenticated = new Authenticated();

    @Override
    public boolean isPermitted(User user) {
        return user != null && ((VertxUser)user.getDelegate()).getPrincipal().getAuthentication() != null;
    }

    public static Authenticated authenticated() {
        return authenticated;
    }
}
