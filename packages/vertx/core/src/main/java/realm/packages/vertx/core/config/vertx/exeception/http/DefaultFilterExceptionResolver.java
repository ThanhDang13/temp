package realm.packages.vertx.core.config.vertx.exeception.http;

import io.vertx.rxjava3.core.http.HttpServerResponse;
import io.vertx.rxjava3.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static realm.packages.vertx.core.extension.http.constant.HttpHeaderValues.TEXT_HTML;

public class DefaultFilterExceptionResolver implements ExceptionResolver {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void resolveException(RoutingContext context, Throwable throwable) {
        HttpServerResponse response = context.response();
        jsonErrorMessage(
                response.setStatusCode(INTERNAL_SERVER_ERROR.value()),
                throwable.getMessage());

        logger.error(throwable.getMessage());
        if (logger.isDebugEnabled()) {
            StringWriter writer = new StringWriter();
            throwable.printStackTrace(new PrintWriter(writer));
            logger.error(writer.toString());
        }
    }

    protected void jsonErrorMessage(HttpServerResponse response, String message) {
        response.putHeader(HttpHeaders.CONTENT_TYPE, TEXT_HTML)
                .end("<h2>error : " + message + "</h2>");
    }
}
