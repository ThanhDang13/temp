package realm.packages.vertx.core.config.vertx.binder.rest;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;
import realm.packages.vertx.core.config.vertx.exception.http.DefaultExceptionResolver;
import realm.packages.vertx.core.config.vertx.exception.http.DefaultFilterExceptionResolver;
import realm.packages.vertx.core.config.vertx.exception.http.ExceptionResolver;
import realm.packages.vertx.core.config.vertx.handler.http.ControllerResultResolver;
import realm.packages.vertx.core.config.vertx.handler.http.DefaultControllerResultResolver;
import realm.packages.vertx.core.config.vertx.security.SecurityResolver;

@Component
@Getter
public class VertxRestApi {

  private ExceptionResolver filterExceptionResolver;

  private ExceptionResolver exceptionResolver;
  private ControllerResultResolver controllerResultResolver;

  private ExceptionResolver webExceptionResolver;
  private ControllerResultResolver webControllerResultResolver;

  private SecurityResolver securityResolver;

  @PostConstruct
  public void init() {
    this.exceptionResolver = new DefaultExceptionResolver();
    this.webExceptionResolver = new DefaultExceptionResolver();
    this.controllerResultResolver = new DefaultControllerResultResolver(this);
    this.filterExceptionResolver = new DefaultFilterExceptionResolver();
    securityResolver = new SecurityResolver();
  }

  public VertxRestApi exceptionResolver(ExceptionResolver exceptionResolver) {
    this.exceptionResolver = exceptionResolver;
    return this;
  }

  public VertxRestApi controllerResultResolver(ControllerResultResolver controllerResultResolver) {
    this.controllerResultResolver = controllerResultResolver;
    return this;
  }

  public VertxRestApi webExceptionResolver(ExceptionResolver exceptionResolver) {
    this.webExceptionResolver = exceptionResolver;
    return this;
  }

  public VertxRestApi webControllerResultResolver(
      ControllerResultResolver controllerResultResolver) {
    this.webControllerResultResolver = controllerResultResolver;
    return this;
  }

  public VertxRestApi filterExceptionResolver(ExceptionResolver exceptionResolver) {
    this.filterExceptionResolver = exceptionResolver;
    return this;
  }

  public VertxRestApi securityResolver(SecurityResolver securityResolver) {
    this.securityResolver = securityResolver;
    return this;
  }
}
