package realm.packages.vertx.core.config.vertx.security.model.principal.impl;

import io.vertx.rxjava3.core.MultiMap;
import lombok.Builder;
import lombok.Data;
import realm.packages.vertx.core.config.vertx.security.model.principal.VertxAuthentication;
import realm.packages.vertx.core.config.vertx.security.model.principal.VertxPrincipal;

@Data
@Builder
public class SimplePrincipal implements VertxPrincipal {

    private VertxAuthentication authentication;
    private Object otherInfo;

    //Define client info
    private MultiMap clientInfo;
    private Object id;

    @Override
    public VertxAuthentication getAuthentication() {
        return authentication;
    }
}
