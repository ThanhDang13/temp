package realm.packages.vertx.core.extension.paging.parser;

import io.vertx.rxjava3.core.http.HttpServerRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import realm.packages.vertx.core.config.vertx.binder.param.parser.ControllerParamParser;
import realm.packages.vertx.core.extension.paging.model.ListWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListWrapperParamParser implements ControllerParamParser<ListWrapper> {

    @Override
    public Class<ListWrapper> type() {
        return ListWrapper.class;
    }

    @Override
    public ListWrapper parser(HttpServerRequest request) {
        ListWrapper listWrapper = new ListWrapper();

        List<Integer> lstId = new ArrayList<>();
        String strId = request.getParam("lstId");
        if (StringUtils.isBlank(strId)) {
            listWrapper.setLstId(lstId);
            return listWrapper;
        }

        try {
            lstId = Arrays.stream(strId.split(",")).map(Integer::valueOf).collect(Collectors.toList());
        } catch (Exception ex) {

        }

        listWrapper.setLstId(lstId);
        return listWrapper;
    }
}
