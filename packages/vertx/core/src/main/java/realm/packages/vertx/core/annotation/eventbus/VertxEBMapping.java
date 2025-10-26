package realm.packages.vertx.core.annotation.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface VertxEBMapping {

  @AliasFor("path")
  String value() default "/";

  @AliasFor("value")
  String path() default "/";
}
