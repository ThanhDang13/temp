package realm.packages.vertx.core.config.vertx.binder.param.converter.impl;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import realm.packages.vertx.core.config.vertx.binder.param.converter.ParamConverter;
import realm.packages.vertx.core.config.vertx.exeception.VertxSpringCoreException;

@Component
public class ParamLongWrapperConverter extends ParamConverter<LongWrapper> {

    @Override
    public LongWrapper convert(Object value) throws VertxSpringCoreException {
        try {
            return new LongWrapper(NumberUtils.createLong(String.valueOf(value)));
        } catch (Exception e) {
            return new LongWrapper(new VertxSpringCoreException("Invalid number value: " + value, HttpStatus.BAD_REQUEST.value()));
        }

    }

    public LongWrapper convertNullValue() {
        return new LongWrapper();
    }

    @Override
    public Class<LongWrapper> getSupportType() {
        return LongWrapper.class;
    }
}
