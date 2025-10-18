package realm.packages.vertx.core.config.vertx.binder.param.converter.factory;

import realm.packages.vertx.core.config.vertx.binder.param.converter.ParamConverter;

public abstract class ParamConverterFactory {
    public abstract <T> ParamConverter getParamConverter(Class<T> clazz);
}
