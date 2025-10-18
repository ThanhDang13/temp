package realm.packages.vertx.core.config.vertx.exeception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends VertxSpringCoreException {

    public AuthenticationException(String message) {
        super(message);
        statusCode = HttpStatus.UNAUTHORIZED.value();
    }
}
