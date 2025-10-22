package realm.packages.vertx.core.config.vertx.filter.security;

import io.reactivex.rxjava3.core.Completable;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import realm.packages.vertx.core.config.vertx.exception.AuthenticationException;
import realm.packages.vertx.core.config.vertx.filter.VertxGenericFilter;
import realm.packages.vertx.core.config.vertx.filter.security.jwt.JWTService;
import realm.packages.vertx.core.config.vertx.security.model.UrlPatternMatcher;
import realm.packages.vertx.core.config.vertx.security.model.level.SecurityLevel;

import java.util.List;
import static java.util.Arrays.asList;

@Data
public class BackAuthenticationExtractingFilter implements VertxGenericFilter {
    @Autowired
    private JWTService jwtService;

    @Override
    public List<UrlPatternMatcher.Request> mappingUrls() {
        return asList(UrlPatternMatcher.Request.from("/*"));
    }

    @Override
    public Completable doFilterRx(RoutingContext routingContext) {
        // CORS allowing
        if (routingContext.request().method().equals(HttpMethod.OPTIONS)) {
            return Completable.complete();
        }

        Boolean isAuthenticated = routingContext.get(SecurityLevel.AUTHENTICATION);

        //1. Có yêu cầu đăng nhập nhưng không xác thực
        //2. Hoặc đã xác thực nhưng token đã hết hạn
        if (isAuthenticated != null) {
            String authorization = routingContext.request().getHeader("Authorization");
            System.out.println("Authorization =>" + authorization);

            if (!isAuthenticated) {
                return Completable.error(new AuthenticationException("un authenticated"));
            }
        }

        return Completable.complete();
    }
}