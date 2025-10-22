package realm.packages.vertx.core.annotation;

import realm.packages.vertx.core.verticle.DefaultSpringVerticle;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableAutoDeployVerticle {
    Class verticle() default DefaultSpringVerticle.class;
}
