package realm.packages.vertx.core.annotation.after;

import java.lang.annotation.*;
import org.springframework.stereotype.Component;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
@Inherited
public @interface VertxAfterHandler {}
