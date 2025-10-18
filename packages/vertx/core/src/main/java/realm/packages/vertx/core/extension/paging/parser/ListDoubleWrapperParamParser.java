package realm.packages.vertx.core.extension.paging.parser;

import io.vertx.rxjava3.core.http.HttpServerRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import realm.packages.vertx.core.config.vertx.binder.param.parser.ControllerParamParser;
import realm.packages.vertx.core.extension.paging.model.ListDoubleWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListDoubleWrapperParamParser implements ControllerParamParser<ListDoubleWrapper> {

    @Override
    public Class<ListDoubleWrapper> type() {
        return ListDoubleWrapper.class;
    }

    @Override
    public ListDoubleWrapper parser(HttpServerRequest request) {
        ListDoubleWrapper listWrapper = new ListDoubleWrapper();

        List<Double> lstId = new ArrayList<>();
        String strId = request.getParam("lstId");
        if (StringUtils.isBlank(strId)) {
            listWrapper.setLstId(lstId);
            return listWrapper;
        }

        try {
            lstId = Arrays.stream(strId.split(",")).map(Double::valueOf).collect(Collectors.toList());
        } catch (Exception ex) {

        }

        listWrapper.setLstId(lstId);
        return listWrapper;
    }
}
