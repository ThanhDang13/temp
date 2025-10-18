package realm.apps.api.rest;

import io.reactivex.rxjava3.core.Single;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import realm.packages.vertx.core.annotation.http.VertxRestController;
import realm.packages.vertx.core.annotation.http.request.mapping.VertxGet;

@VertxRestController
public class TestApi {
    @VertxGet("/api/ping")
    public Single<ResponseEntity<String>> ping() {
        return Single.just(ResponseEntity.ok("pong"));
    }
}
