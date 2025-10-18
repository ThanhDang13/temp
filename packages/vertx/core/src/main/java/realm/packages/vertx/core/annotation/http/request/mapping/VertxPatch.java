package realm.packages.vertx.core.annotation.http.request.mapping;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@VertxRequestMapping(method = VertxHttpMethod.PATCH)
public @interface VertxPatch {

    @AliasFor(annotation = VertxRequestMapping.class, attribute = "path")
    String value() default "/";
}
