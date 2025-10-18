package realm.packages.vertx.core.config.vertx.exeception.http;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.http.HttpServerResponse;
import io.vertx.rxjava3.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import realm.packages.vertx.core.config.vertx.exeception.VertxSpringCoreException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static realm.packages.vertx.core.extension.http.constant.HttpHeaderValues.APPLICATION_JSON_UTF8;

public class DefaultExceptionResolver implements ExceptionResolver {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void resolveException(RoutingContext context, Throwable throwable) {
        HttpServerResponse response = context.response();
        if (throwable instanceof VertxSpringCoreException) {
            jsonErrorMessage(
                    response.setStatusCode(((VertxSpringCoreException) throwable).getStatusCode()),
                    throwable.getMessage());
        } else {
            jsonErrorMessage(
                    response.setStatusCode(INTERNAL_SERVER_ERROR.value()),
                    throwable.getMessage());
        }

        logger.error(throwable.getMessage());
        if (logger.isDebugEnabled()) {
            StringWriter writer = new StringWriter();
            throwable.printStackTrace(new PrintWriter(writer));
            logger.error(writer.toString());
        }
    }

    protected void jsonErrorMessage(HttpServerResponse response, String message) {
        response.putHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_UTF8)
                .end(new JsonObject().put("error", message).toString());
    }
}