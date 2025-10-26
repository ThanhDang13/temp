package realm.packages.vertx.core.extension.paging.parser;

import io.vertx.rxjava3.core.http.HttpServerRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import realm.packages.vertx.core.config.vertx.binder.param.parser.ControllerParamParser;
import realm.packages.vertx.core.extension.paging.model.ListBooleanWrapper;

@Component
public class ListBooleanWrapperParamParser implements ControllerParamParser<ListBooleanWrapper> {

  @Override
  public Class<ListBooleanWrapper> type() {
    return ListBooleanWrapper.class;
  }

  @Override
  public ListBooleanWrapper parser(HttpServerRequest request) {
    ListBooleanWrapper listWrapper = new ListBooleanWrapper();

    List<Boolean> lstId = new ArrayList<>();
    String strId = request.getParam("lstId");
    if (StringUtils.isBlank(strId)) {
      listWrapper.setLstId(lstId);
      return listWrapper;
    }

    try {
      lstId = Arrays.stream(strId.split(",")).map(Boolean::valueOf).collect(Collectors.toList());
    } catch (Exception ex) {

    }

    listWrapper.setLstId(lstId);
    return listWrapper;
  }
}
