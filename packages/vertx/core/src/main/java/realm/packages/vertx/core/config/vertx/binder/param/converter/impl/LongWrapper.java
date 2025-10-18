package realm.packages.vertx.core.config.vertx.binder.param.converter.impl;

import lombok.Getter;
import lombok.Setter;
import realm.packages.vertx.core.config.vertx.exeception.VertxSpringCoreException;

@Getter
@Setter
public class LongWrapper {

    private Long value;
    private VertxSpringCoreException exception;

    public LongWrapper() {
    }

    public LongWrapper(Long value) {
        this.value = value;
    }

    public LongWrapper(VertxSpringCoreException exception) {
        this.exception = exception;
    }
}
