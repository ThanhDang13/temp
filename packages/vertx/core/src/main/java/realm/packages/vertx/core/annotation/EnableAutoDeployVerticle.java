package realm.packages.vertx.core.annotation;

import java.lang.annotation.*;
import realm.packages.vertx.core.verticle.DefaultSpringVerticle;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableAutoDeployVerticle {
  Class verticle() default DefaultSpringVerticle.class;
}
