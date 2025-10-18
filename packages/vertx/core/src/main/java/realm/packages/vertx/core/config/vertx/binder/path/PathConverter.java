package realm.packages.vertx.core.config.vertx.binder.path;

import org.springframework.stereotype.Component;
import realm.packages.vertx.core.annotation.http.request.mapping.VertxRequestMapping;

import java.util.Objects;

/**
 * Convert path from spring, swagger standard to vertx standard
 * Eg: /api/{user_id} => /api/:user_id
 */
@Component
public class PathConverter {

    public String convertPath(String originalPath) {
        Objects.requireNonNull(originalPath);

        originalPath = originalPath
                .replaceAll("\\{", ":")
                .replaceAll("\\}", "");

        return originalPath;
    }

    public String convertPathIdentity(VertxRequestMapping requestMapping) {
        String originalPath = requestMapping.method() + ":" + requestMapping.path();
        Objects.requireNonNull(originalPath);

        originalPath = originalPath
                .replaceAll("\\{", ":")
                .replaceAll("\\}", "");
        return originalPath;
    }
}
