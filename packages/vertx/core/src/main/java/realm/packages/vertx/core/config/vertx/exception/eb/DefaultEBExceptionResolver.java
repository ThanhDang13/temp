package realm.packages.vertx.core.config.vertx.exception.eb;

import io.vertx.rxjava3.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class DefaultEBExceptionResolver implements EBExceptionResolver {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void resolveException(Message<Object> message, Throwable throwable) {
        logger.error(throwable.getMessage());
        if (logger.isDebugEnabled()) {
            StringWriter writer = new StringWriter();
            throwable.printStackTrace(new PrintWriter(writer));
            logger.error(writer.toString());
        }

        message.fail(INTERNAL_SERVER_ERROR.value(), throwable.getMessage());
    }
}
