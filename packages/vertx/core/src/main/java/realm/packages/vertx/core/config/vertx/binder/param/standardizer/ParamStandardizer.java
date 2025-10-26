package realm.packages.vertx.core.config.vertx.binder.param.standardizer;

import static java.util.stream.Collectors.toMap;
import static org.springframework.core.annotation.AnnotatedElementUtils.isAnnotated;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import realm.packages.vertx.core.config.vertx.exception.VertxSpringCoreException;

@Component
public class ParamStandardizer {
  private Map<String, Pair<Method, IParamStandardizer>> paramMethodMap = new HashMap<>();

  @Autowired(required = false)
  private List<IParamStandardizer> paramStandardizers = new ArrayList<>();

  @PostConstruct
  public void scanParam() {
    paramStandardizers.stream()
        .map(this::filterParamToStandardizer)
        .forEach(map -> paramMethodMap.putAll(map));
  }

  private Map<String, Pair<Method, IParamStandardizer>> filterParamToStandardizer(
      IParamStandardizer clazz) {
    return Arrays.stream(clazz.getClass().getMethods())
        .filter(method -> isAnnotated(method, ParameterName.class))
        .flatMap(
            method ->
                Arrays.stream(AnnotationUtils.findAnnotation(method, ParameterName.class).value())
                    .map(s -> Pair.of(s, Pair.of(method, clazz))))
        .collect(toMap(Pair::getLeft, Pair::getRight));
  }

  public String standardize(String key, String value) throws VertxSpringCoreException {
    Pair<Method, IParamStandardizer> pair = paramMethodMap.getOrDefault(key, null);
    try {
      return pair == null ? value : (String) pair.getLeft().invoke(pair.getRight(), value);
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
      throw new VertxSpringCoreException(
          "fail to standardize parameter", HttpStatus.BAD_REQUEST.value());
    }
  }
}
