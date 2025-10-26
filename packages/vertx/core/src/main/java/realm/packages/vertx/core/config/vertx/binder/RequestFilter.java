package realm.packages.vertx.core.config.vertx.binder;

import io.vertx.rxjava3.ext.web.Route;
import io.vertx.rxjava3.ext.web.Router;
import java.util.Comparator;
import java.util.List;
import org.springframework.context.ApplicationContext;
import realm.packages.vertx.core.config.vertx.binder.path.PathConverter;
import realm.packages.vertx.core.config.vertx.binder.rest.VertxRestApi;
import realm.packages.vertx.core.config.vertx.exception.http.ExceptionResolver;
import realm.packages.vertx.core.config.vertx.filter.VertxFilterStrategy;
import realm.packages.vertx.core.config.vertx.filter.VertxGenericFilter;
import realm.packages.vertx.core.config.vertx.security.VertxSecurityProvider;
import realm.packages.vertx.core.config.vertx.security.model.UrlPatternMatcher;

public class RequestFilter {

  private PathConverter pathConverter;
  private VertxFilterStrategy filterResolver;
  private VertxRestApi vertxRestApi;
  private VertxSecurityProvider securityProvider;

  private ExceptionResolver filterExceptionResolver;

  public RequestFilter(ApplicationContext context, ExceptionResolver filterExceptionResolver) {
    this.pathConverter = context.getBean(PathConverter.class);
    this.filterResolver = context.getBean(VertxFilterStrategy.class);
    this.vertxRestApi = context.getBean(VertxRestApi.class);
    this.securityProvider = context.getBean(VertxSecurityProvider.class);

    this.filterExceptionResolver = filterExceptionResolver;
  }

  /**
   * Support Filtering Spring like. Filters are implemented VertxGenericFilter interface Requests
   * comes to filters first. This method sorts filter by VertxGenericFilter.order() and bind
   * VertxGenericFilter.mappingUrls() into Router for internal processing.
   *
   * <p>After all custom filter. Add a final security filter at the end in order to checking
   * authentications
   *
   * @param router
   */
  public void bindFilters(Router router) {
    filterResolver.getVertxGenericFilters().stream()
        .sorted(Comparator.comparing(VertxGenericFilter::order))
        .forEach(filter -> bindSingleFilter(router, filter));

    // bind security filters at final
    List<VertxGenericFilter> securityFilter =
        securityProvider.generateSecurityFilters(vertxRestApi.getSecurityResolver());
    securityFilter.forEach(filter -> bindSingleFilter(router, filter));
  }

  private void bindSingleFilter(Router router, VertxGenericFilter filter) {
    filter.mappingUrls().forEach(request -> bindFilterRouting(router, filter, request));
  }

  /**
   * the processing method of VertxGenericFilter is doFilter/doFilterRx in case doFilterRx returns
   * Completable success then router.next() to the next filter or controller or returns Completable
   * error in case filter got some exceptions
   *
   * @param router
   * @param filter
   * @param request
   * @return
   */
  private Route bindFilterRouting(
      Router router, VertxGenericFilter filter, UrlPatternMatcher.Request request) {
    return routeFilterRequest(router, request)
        .handler(
            routingContext ->
                filter
                    .doFilterRx(routingContext)
                    .subscribe(
                        () -> {
                          routingContext.request().resume();
                          routingContext.next();
                        },
                        throwable ->
                            filterExceptionResolver.resolveException(routingContext, throwable)));
  }

  private Route routeFilterRequest(Router router, UrlPatternMatcher.Request request) {
    String urlPattern = pathConverter.convertPath(request.getUrlPatter());
    if (request.getHttpMethod() == null) {
      return router.route(urlPattern);
    } else {
      return router.route(request.getHttpMethod(), urlPattern);
    }
  }
}
