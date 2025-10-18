package realm.packages.vertx.core.config.vertx.binder.param.converter.factory.impl;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import realm.packages.vertx.core.config.vertx.binder.param.converter.ParamConverter;
import realm.packages.vertx.core.config.vertx.binder.param.converter.factory.ParamConverterFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ParamConvertDefaultFactory extends ParamConverterFactory {

    private final Map<Class, ParamConverter> map = new HashMap<>();

    @Autowired private List<ParamConverter> paramConverters;

    @PostConstruct
    public void init() {
        paramConverters.forEach(pc -> map.put(pc.getSupportType(), pc));
    }

    @Override
    public <T> ParamConverter getParamConverter(Class<T> clazz) {
        ParamConverter paramConverter = map.get(clazz);
        if (paramConverter == null) {
            throw new UnsupportedOperationException("param binder doesn't support this type of value, please add the additional implementation");
        }

        return paramConverter;
    }
}
