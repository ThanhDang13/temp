package realm.packages.vertx.core.verticle;

import io.vertx.rxjava3.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

import static realm.packages.vertx.core.verticle.utils.HttpRequestUtils.correct;

@Component
@Scope("prototype")
public class DefaultSpringVerticle extends AbstractSpringVerticle {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onStart() {
        server = vertx.createHttpServer();
        router = Router.router(vertx);

        server.requestStream()
                .toFlowable()
                .subscribe(request -> {
                    try {
                        router.handle(request); // sửa accept -> handle
                    } catch (Exception e) {
                        try {
                            Field absUri = request.getDelegate().getClass().getDeclaredField("uri");
                            Field absPath = request.getDelegate().getClass().getDeclaredField("path");
                            absUri.setAccessible(true);
                            absPath.setAccessible(true);
                            absUri.set(request.getDelegate(), correct(request.uri()));
                            absPath.set(request.getDelegate(), correct(request.path()));
                            router.handle(request); // sửa accept -> handle
                        } catch (Exception ex) {
                            logger.error("Failed to handle request after correction", ex);
                            request.response().setStatusCode(500).end("Internal Server Error");
                        }
                    }
                });

        server.rxListen(
                env.getProperty("vertx.server.port", Integer.class, 0)
                )
                .subscribe(server -> {
                    logger.info("RealmTruyen: Http Server is open on port " + server.actualPort());
                });
    }
}
