package realm.packages.vertx.core.config.vertx.binder.param.converter.impl;

import lombok.Getter;
import lombok.Setter;
import realm.packages.vertx.core.config.vertx.exception.VertxSpringCoreException;

@Getter
@Setter
public class IntegerWrapper {
  private Integer value;
  private VertxSpringCoreException exception;

  public IntegerWrapper() {}

  public IntegerWrapper(Integer value) {
    this.value = value;
  }

  public IntegerWrapper(VertxSpringCoreException exception) {
    this.exception = exception;
  }
}
