package realm.packages.vertx.core.config.vertx.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static realm.packages.vertx.core.template.VertxExecution.rxSubscribeIndependentObservable;

import io.reactivex.rxjava3.core.Single;
import java.util.function.Supplier;

public class VertxSpringCoreException extends Exception {

  protected int statusCode = INTERNAL_SERVER_ERROR.value();

  public VertxSpringCoreException(String message) {
    super(message);
  }

  public VertxSpringCoreException(String message, int statusCode) {
    super(message);
    this.statusCode = statusCode;
  }

  public VertxSpringCoreException(ErrorCode errorCode) {
    super(errorCode.message);
    this.statusCode = errorCode.code;
  }

  public VertxSpringCoreException(String message, Supplier<Single> callBack) {
    this(message, INTERNAL_SERVER_ERROR.value(), callBack);
  }

  public VertxSpringCoreException(String message, int statusCode, Supplier<Single> callBack) {
    super(message);
    this.statusCode = statusCode;
    rxSubscribeIndependentObservable(callBack.get());
  }

  public int getStatusCode() {
    return statusCode;
  }
}
