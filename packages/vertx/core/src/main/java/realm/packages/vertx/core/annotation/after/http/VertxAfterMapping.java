package realm.packages.vertx.core.annotation.after.http;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface VertxAfterMapping {

    String[] value() default {};
}
