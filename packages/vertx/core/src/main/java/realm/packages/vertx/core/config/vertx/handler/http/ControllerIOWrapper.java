package realm.packages.vertx.core.config.vertx.handler.http;

import io.vertx.rxjava3.ext.web.RoutingContext;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

@Data
public class ControllerIOWrapper {

  private Object[] args;
  private Object returnValue;
  private RoutingContext routingContext;
  private List<Pair<Method, Object>> afterHandlers;
  private long startProcessTimestamp;
  private long endProcessTimestamp;
  private String pathPattern;
  private Map<String, Object> clientInfo;

  public ControllerIOWrapper() {}

  public ControllerIOWrapper(
      Object[] args,
      Object returnValue,
      RoutingContext routingContext,
      List<Pair<Method, Object>> afterHandlers) {
    this.args = args;
    this.returnValue = returnValue;
    this.routingContext = routingContext;
    this.afterHandlers = afterHandlers;
  }
}
