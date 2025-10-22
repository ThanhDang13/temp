package realm.packages.vertx.core.config.vertx.binder;

import io.reactivex.rxjava3.core.Maybe;
import io.vertx.rxjava3.core.eventbus.EventBus;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import realm.packages.vertx.core.annotation.eventbus.VertxEBConsumer;
import realm.packages.vertx.core.annotation.eventbus.VertxEBMapping;
import realm.packages.vertx.core.config.application.ApplicationConfigModel;
import realm.packages.vertx.core.config.vertx.binder.eb.VertxEBApi;
import realm.packages.vertx.core.config.vertx.binder.eb.VertxEBApiConfigurer;
import realm.packages.vertx.core.config.vertx.exception.VertxSpringCoreException;
import realm.packages.vertx.core.config.vertx.exception.eb.EBExceptionResolver;
import realm.packages.vertx.core.config.vertx.handler.eb.ConsumerEBResultResolver;

import java.lang.reflect.Method;
import java.util.stream.Stream;

@Component
public class VertxEBCenterBinder {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired private ApplicationContext context;
    @Autowired private ApplicationConfigModel applicationConfigModel;
    @Autowired private VertxEBApi vertxEBApi;

    private EBExceptionResolver exceptionResolver;
    private ConsumerEBResultResolver consumerEBResultResolver;
    private ConsumerEBResultResolver consumerEBCompleteResolver;

    @PostConstruct
    public void initRestResolvers() {

        try {
            VertxEBApiConfigurer externalConfig = context.getBean(VertxEBApiConfigurer.class);
            if (externalConfig != null) {
                externalConfig.config(vertxEBApi);
            }
        } catch (NoSuchBeanDefinitionException e) {}

        exceptionResolver = vertxEBApi.getExceptionResolver();
        consumerEBResultResolver = vertxEBApi.getConsumerEBResultResolver();
        consumerEBCompleteResolver = vertxEBApi.getConsumerEBComplteResolver();
    }

    public void bind(EventBus eventBus) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(VertxEBConsumer.class));

        for (BeanDefinition beanDefinition : scanner.findCandidateComponents(applicationConfigModel.getBasePackage())) {
            bindEB(beanDefinition, eventBus);
        }
    }

    private void bindEB(BeanDefinition beanDefinition, EventBus eventBus) {
        try {
            Class<?> beanClass = Class.forName(beanDefinition.getBeanClassName());
            Object beanObj = context.getBean(beanClass);
            Stream.of(beanClass.getMethods())
                    .filter(method -> AnnotatedElementUtils.isAnnotated(method, VertxEBMapping.class))
                    .forEach(method -> bindEB(eventBus, beanObj, method));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindEB(EventBus eventBus, Object beanObj, Method method) {
        VertxEBMapping ebMapping = AnnotatedElementUtils.getMergedAnnotation(method, VertxEBMapping.class);

        eventBus.consumer(ebMapping.path())
                .toObservable()
                .subscribe(message -> {
                    try {
                        Object returnValue = method.invoke(beanObj, message.body());
                        if (returnValue instanceof Maybe) {
                            Maybe<Object> maybe = (Maybe<Object>) returnValue;
                            maybe.subscribe(
                                    result -> consumerEBResultResolver.resolve(message, result), // return result back to sender
                                    throwable -> exceptionResolver.resolveException(message, throwable), // handle exception
                                    () -> consumerEBCompleteResolver.resolve(message, null)); // handle complete but no result returned
                        } else {
                            exceptionResolver.resolveException(message, new VertxSpringCoreException("return value from event bus should be Maybe"));
                        }
                    } catch (Exception e) {
                        exceptionResolver.resolveException(message, e);
                    }
                });
    }
}