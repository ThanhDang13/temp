package realm.packages.vertx.core.config.vertx.binder.param.converter.impl;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import realm.packages.vertx.core.config.vertx.binder.param.converter.ParamConverter;
import realm.packages.vertx.core.config.vertx.exception.VertxSpringCoreException;

@Component
public class ParamFloatConverter extends ParamConverter<Float> {

    @Override
    public Float convert(Object value) throws VertxSpringCoreException {
        try {
            return NumberUtils.createFloat(String.valueOf(value));
        } catch (Exception e) {
            throw new VertxSpringCoreException("Invalid number value: " + value, HttpStatus.BAD_REQUEST.value());
        }
    }

    @Override
    public Class<Float> getSupportType() {
        return Float.class;
    }
}
