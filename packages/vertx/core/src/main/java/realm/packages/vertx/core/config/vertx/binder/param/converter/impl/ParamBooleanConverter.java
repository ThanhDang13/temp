package realm.packages.vertx.core.config.vertx.binder.param.converter.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import realm.packages.vertx.core.config.vertx.binder.param.converter.ParamConverter;
import realm.packages.vertx.core.config.vertx.exception.VertxSpringCoreException;

@Component
public class ParamBooleanConverter extends ParamConverter<Boolean> {

    @Override
    public Boolean convert(Object value) throws VertxSpringCoreException {
        try {
            return Boolean.valueOf(String.valueOf(value));
        } catch (Exception e) {
            throw new VertxSpringCoreException("Invalid boolean value: " + value, HttpStatus.BAD_REQUEST.value());
        }
    }

    @Override
    public Class<Boolean> getSupportType() {
        return Boolean.class;
    }
}
