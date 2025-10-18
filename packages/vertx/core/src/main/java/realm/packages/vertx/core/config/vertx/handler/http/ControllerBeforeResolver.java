package realm.packages.vertx.core.config.vertx.handler.http;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.rxjava3.core.http.HttpServerRequest;
import org.apache.commons.lang3.tuple.Pair;
import realm.packages.vertx.core.config.vertx.exeception.VertxSpringCoreException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

public interface ControllerBeforeResolver {
    Completable resolve(List<Pair<Method, Object>> beforeHandlers, Map<Parameter, Object> args, HttpServerRequest request)
            throws InvocationTargetException, IllegalAccessException, VertxSpringCoreException;
}
