package realm.packages.vertx.core.config.vertx.security.model.principal.impl;

import realm.packages.vertx.core.config.vertx.security.model.principal.VertxAuthentication;

import java.util.List;

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
