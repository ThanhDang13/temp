package realm.packages.vertx.core.extension.paging.parser;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.math.NumberUtils.toInt;

import io.vertx.rxjava3.core.http.HttpServerRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import realm.packages.vertx.core.config.vertx.binder.param.parser.ControllerParamParser;
import realm.packages.vertx.core.extension.paging.model.Order;
import realm.packages.vertx.core.extension.paging.model.Pageable;

@Component
public class PageAbleParamParser implements ControllerParamParser<Pageable> {

  @Override
  public Class<Pageable> type() {
    return Pageable.class;
  }

  @Override
  public Pageable parser(HttpServerRequest request) {
    Pageable pageable = new Pageable();

    int page = toInt(request.getParam("page"), Pageable.DEFAULT_PAGE);
    pageable.setPage(page);

    int pageSize = toInt(request.getParam("limit"), Pageable.DEFAULT_LIMIT);
    pageSize = pageSize < 0 ? Pageable.MAXIMUM_LIMIT : pageSize;
    pageable.setLimit(pageSize);

    pageable.setOffset(toInt(request.getParam("offset"), (page - 1) * pageSize));

    List<Order> orders = getOrder(request.params().getAll("sort"));
    if (!orders.isEmpty()) {
      pageable.setSort(orders);
    }
    Set<String> paramNames = request.params().names();
    Map<String, String> params =
        paramNames.stream()
            .map(
                s -> {
                  String value = request.getParam(s);
                  if (StringUtils.isEmpty(value)) {
                    return null;
                  }
                  return Pair.of(s, value);
                })
            .filter(Objects::nonNull)
            .collect(toMap(Pair::getKey, Pair::getValue));
    pageable.setParams(params);
    return pageable;
  }

  private static List<Order> getOrder(List<String> orders) {
    if (orders == null) return null;

    return orders.stream()
        .filter(Objects::nonNull)
        .map(PageAbleParamParser::getOrder)
        .filter(Objects::nonNull)
        .collect(toList());
  }

  private static Order getOrder(String order) {
    String[] arr = order.split(",");

    if (arr.length == 1) {
      return new Order(arr[0], Order.Direction.DESC);
    }

    if (arr.length != 2) return null;

    if (arr[1].toLowerCase().equals("desc")) return new Order(arr[0], Order.Direction.DESC);

    return new Order(arr[0], Order.Direction.ASC);
  }
}
