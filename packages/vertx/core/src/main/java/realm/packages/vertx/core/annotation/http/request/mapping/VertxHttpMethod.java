package realm.packages.vertx.core.annotation.http.request.mapping;

import io.vertx.core.http.HttpMethod;

public enum VertxHttpMethod {
    GET(HttpMethod.GET),
    POST(HttpMethod.POST),
    PUT(HttpMethod.PUT),
    DELETE(HttpMethod.DELETE),
    PATCH(HttpMethod.PATCH);

    private final HttpMethod vertxMethod;

    VertxHttpMethod(HttpMethod vertxMethod) {
        this.vertxMethod = vertxMethod;
    }

    public HttpMethod getVertxMethod() {
        return vertxMethod;
    }
}
