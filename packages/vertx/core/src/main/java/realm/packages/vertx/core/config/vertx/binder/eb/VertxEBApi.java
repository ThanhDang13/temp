package realm.packages.vertx.core.config.vertx.binder.eb;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;
import realm.packages.vertx.core.config.vertx.exception.eb.DefaultEBExceptionResolver;
import realm.packages.vertx.core.config.vertx.exception.eb.EBExceptionResolver;
import realm.packages.vertx.core.config.vertx.handler.eb.ConsumerEBResultResolver;
import realm.packages.vertx.core.config.vertx.handler.eb.DefaultEBCompleteResolver;
import realm.packages.vertx.core.config.vertx.handler.eb.DefaultEBResultResolver;

@Component
@Getter
public class VertxEBApi {

    private EBExceptionResolver exceptionResolver;
    private ConsumerEBResultResolver consumerEBResultResolver;
    private ConsumerEBResultResolver consumerEBComplteResolver;

    @PostConstruct
    public void init() {
        this.exceptionResolver = new DefaultEBExceptionResolver();
        this.consumerEBResultResolver = new DefaultEBResultResolver();
        this.consumerEBComplteResolver = new DefaultEBCompleteResolver();
    }

    public VertxEBApi ebExceptionResolver(DefaultEBExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
        return this;
    }

    public VertxEBApi ebResultResolver(ConsumerEBResultResolver consumerEBResultResolver) {
        this.consumerEBResultResolver = consumerEBResultResolver;
        return this;
    }

    public VertxEBApi ebComplteResolver(ConsumerEBResultResolver consumerEBComplteResolver) {
        this.consumerEBComplteResolver = consumerEBComplteResolver;
        return this;
    }
}
