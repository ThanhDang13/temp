package realm.packages.vertx.core.annotation.http.request.mapping;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Mapping
public @interface VertxRequestMapping {
    VertxHttpMethod method() default VertxHttpMethod.GET;

    @AliasFor("path")
    String value() default "/";

    @AliasFor("value")
    String path() default "/";
}
