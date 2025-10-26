package realm.packages.vertx.core.config.vertx.exception.eb;

import io.vertx.rxjava3.core.eventbus.Message;

public interface EBExceptionResolver {
  public void resolveException(Message<Object> message, Throwable throwable);
}
