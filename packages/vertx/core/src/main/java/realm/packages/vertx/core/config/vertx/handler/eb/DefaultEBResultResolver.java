package realm.packages.vertx.core.config.vertx.handler.eb;

import io.vertx.rxjava3.core.eventbus.Message;

public class DefaultEBResultResolver implements ConsumerEBResultResolver {

  @Override
  public void resolve(Message<Object> message, Object result) {
    message.reply(result);
  }
}
