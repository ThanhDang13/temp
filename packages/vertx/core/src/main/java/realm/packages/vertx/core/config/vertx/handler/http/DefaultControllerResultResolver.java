package realm.packages.vertx.core.config.vertx.handler.http;

import static io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.reactivex.rxjava3.core.Single;
import io.vertx.rxjava3.core.http.HttpServerResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import realm.packages.vertx.core.config.jackson.DefaultJackson;
import realm.packages.vertx.core.config.vertx.binder.rest.VertxRestApi;
import realm.packages.vertx.core.config.vertx.exception.VertxSpringCoreException;

public class DefaultControllerResultResolver implements ControllerResultResolver {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  protected VertxRestApi vertxRestApi;

  public DefaultControllerResultResolver(VertxRestApi vertxRestApi) {
    this.vertxRestApi = vertxRestApi;
  }

  @Override
  public void resolve(ControllerIOWrapper ioWrapper) {
    Object returnValue = ioWrapper.getReturnValue();
    HttpServerResponse response = ioWrapper.getRoutingContext().response();
    if (returnValue instanceof Single) {
      Single<ResponseEntity> asyncResult = (Single<ResponseEntity>) returnValue;
      asyncResult
          .map(responseEntity -> handleResponse(response, responseEntity))
          .subscribe(
              responseBodyOptional -> {
                Object responseBody = responseBodyOptional.orElse(null);
                ioWrapper.setReturnValue(responseBody);

                // Cho phép xử lý tiếp chuỗi event tiếp theo
                afterHandlerResolver(ioWrapper);
              },
              throwable ->
                  vertxRestApi
                      .getExceptionResolver()
                      .resolveException(ioWrapper.getRoutingContext(), throwable));
    } else {
      vertxRestApi
          .getExceptionResolver()
          .resolveException(
              ioWrapper.getRoutingContext(),
              new VertxSpringCoreException("unsupported return type (not Single)"));
    }
  }

  private Optional<Object> handleResponse(
      HttpServerResponse response, ResponseEntity responseEntity) throws Exception {
    Object responseBody = responseEntity.getBody();
    if (responseBody == null) {
      nullBodyResponse(response);
    } else {
      nonNullBodyResponse(response, responseBody, responseEntity);
    }
    return Optional.ofNullable(responseBody);
  }

  protected void afterHandlerResolver(ControllerIOWrapper ioWrapper) {
    ioWrapper.setEndProcessTimestamp(System.currentTimeMillis());
    List<Pair<Method, Object>> afterHandlers = ioWrapper.getAfterHandlers();
    if (!isEmpty(afterHandlers)) {
      for (Pair<Method, Object> pair : afterHandlers) {
        try {
          Method method = pair.getLeft();
          Object handler = pair.getRight();
          int argsLength = method.getParameters().length;
          if (argsLength == 1) {
            if (method.getParameters()[0].getType().equals(ControllerIOWrapper.class)) {
              method.invoke(handler, ioWrapper);
            } else {
              method.invoke(handler, ioWrapper.getReturnValue(), ioWrapper.getArgs());
            }
          } else {
            Object[] args = new Object[argsLength];
            args[0] = ioWrapper.getReturnValue();
            System.arraycopy(ioWrapper.getArgs(), 0, args, 1, argsLength - 1);
            method.invoke(handler, args);
          }
        } catch (Exception e) {
          logger.error("after invoker got exception: " + e.getMessage());
          logger.error(errorStackTraceString(e));
        }
      }
    }
  }

  @SuppressWarnings("do not block thread in this method")
  protected void nonNullBodyResponse(
      HttpServerResponse response, Object responseBody, ResponseEntity responseEntity)
      throws JsonProcessingException {
    if (!(responseBody instanceof String)) {
      responseBody = DefaultJackson.objectMapper().writeValueAsString(responseBody);
    }

    Iterator<Map.Entry<String, String>> iter =
        responseEntity.getHeaders().toSingleValueMap().entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry<String, String> e = iter.next();
      response = response.putHeader(e.getKey(), e.getValue());
    }

    response
        .setStatusCode(responseEntity.getStatusCodeValue())
        .putHeader(CONTENT_TYPE.toString(), APPLICATION_JSON)
        .end(responseBody.toString());
  }

  @SuppressWarnings("do not block thread in this method")
  protected void nullBodyResponse(HttpServerResponse response) {
    response
        .setStatusCode(NOT_FOUND.value())
        .putHeader(CONTENT_TYPE.toString(), APPLICATION_JSON)
        .end();
  }

  private static String errorStackTraceString(Exception e) {
    StringWriter writer = new StringWriter();
    e.printStackTrace(new PrintWriter(writer));
    return writer.toString();
  }
}
