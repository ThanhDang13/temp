package realm.packages.vertx.core.config.vertx.handler.http;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.rxjava3.core.http.HttpServerRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

import static org.springframework.util.CollectionUtils.isEmpty;

@Component
public class DefaultControllerBeforeResolver implements ControllerBeforeResolver {

    public Completable resolve(List<Pair<Method, Object>> beforeHandlers, Map<Parameter, Object> args, HttpServerRequest request)
            throws InvocationTargetException, IllegalAccessException {
        if (isEmpty(beforeHandlers)) return Completable.complete();

        return chain(beforeHandlers, args, 0);
    }

    private Completable chain(List<Pair<Method, Object>> beforeHandlers, Map<Parameter,Object> args, int index) throws InvocationTargetException, IllegalAccessException {
        Pair<Method, Object> handler = beforeHandlers.get(index);
        Completable completable = (Completable) handler.getLeft().invoke(handler.getRight(), args.values().toArray(new Object[0]));
        if (index == beforeHandlers.size() - 1) {
            return completable;
        } else {
            return completable
                    .andThen(chain(beforeHandlers, args, index + 1));
        }
    }
}
