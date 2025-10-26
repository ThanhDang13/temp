package realm.packages.vertx.core.config.vertx.handler.eb;

import io.vertx.rxjava3.core.eventbus.Message;

public interface ConsumerEBResultResolver {

  public void resolve(Message<Object> message, Object returnValue);
}
