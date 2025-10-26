package realm.packages.vertx.core.config.vertx.handler.eb;

import io.vertx.rxjava3.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultEBCompleteResolver implements ConsumerEBResultResolver {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public void resolve(Message<Object> message, Object result) {
    if (logger.isDebugEnabled()) {
      logger.debug("EB success and no reply result, just reply a completable");
    }
  }
}
