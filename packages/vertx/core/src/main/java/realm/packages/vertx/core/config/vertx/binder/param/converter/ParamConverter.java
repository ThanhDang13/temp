package realm.packages.vertx.core.config.vertx.binder.param.converter;

import realm.packages.vertx.core.config.vertx.exception.VertxSpringCoreException;

public abstract class ParamConverter<T> {

  public abstract T convert(Object value) throws VertxSpringCoreException;

  public T convertNullValue() {
    return null;
  }

  public abstract Class<T> getSupportType();
}
