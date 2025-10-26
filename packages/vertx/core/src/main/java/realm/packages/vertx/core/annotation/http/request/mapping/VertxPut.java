package realm.packages.vertx.core.annotation.http.request.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@VertxRequestMapping(method = VertxHttpMethod.PUT)
public @interface VertxPut {

  @AliasFor(annotation = VertxRequestMapping.class, attribute = "path")
  String value() default "/";
}
