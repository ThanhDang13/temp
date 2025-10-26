package realm.packages.vertx.core.config.vertx.binder.param.parser;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.http.HttpServerRequest;
import java.util.Set;

public interface ControllerParamParser<T> {

  public Class<T> type();

  public T parser(HttpServerRequest request);

  default JsonObject getParams(HttpServerRequest request) {
    Set<String> names = request.params().names();
    JsonObject entries = new JsonObject();
    names.forEach(s -> entries.put(s, request.getParam(s)));
    return entries;
  }
}
