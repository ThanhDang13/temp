package realm.packages.vertx.core.config.vertx.binder.param.converter.impl;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class StringWrapper {

    private String content;

    public StringWrapper() {
    }

    public StringWrapper(String content) {
        this.content = content;
    }
}
