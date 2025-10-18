package realm.packages.vertx.core.config.vertx.binder.param.converter.impl;

import org.springframework.stereotype.Component;
import realm.packages.vertx.core.config.vertx.binder.param.converter.ParamConverter;

@Component
public class ParamStringConverter extends ParamConverter<String> {

    @Override
    public String convert(Object value) {
        return String.valueOf(value);
    }

    @Override
    public Class<String> getSupportType() {
        return String.class;
    }
}
