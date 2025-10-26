package realm.packages.vertx.core.config.vertx.binder.param.converter.impl;

import org.springframework.stereotype.Component;
import realm.packages.vertx.core.config.vertx.binder.param.converter.ParamConverter;

@Component
public class ParamStringWrapperConverter extends ParamConverter<StringWrapper> {

  @Override
  public StringWrapper convert(Object value) {
    return new StringWrapper((String.valueOf(value)));
  }

  public StringWrapper convertNullValue() {
    return new StringWrapper();
  }

  @Override
  public Class<StringWrapper> getSupportType() {
    return StringWrapper.class;
  }
}
