package realm.packages.vertx.core.config.vertx.binder;

import io.vertx.rxjava3.ext.web.Router;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import realm.packages.vertx.core.annotation.after.VertxAfterHandler;
import realm.packages.vertx.core.annotation.after.VertxBeforeHandler;
import realm.packages.vertx.core.annotation.after.http.VertxAfterMapping;
import realm.packages.vertx.core.annotation.after.http.VertxBeforeMapping;
import realm.packages.vertx.core.annotation.http.VertxRestController;
import realm.packages.vertx.core.annotation.http.VertxWebController;
import realm.packages.vertx.core.annotation.http.request.mapping.VertxRequestMapping;
import realm.packages.vertx.core.config.application.ApplicationConfigModel;
import realm.packages.vertx.core.config.vertx.binder.param.extractor.ParamExtractor;
import realm.packages.vertx.core.config.vertx.binder.path.PathConverter;
import realm.packages.vertx.core.config.vertx.binder.rest.VertxRestApi;
import realm.packages.vertx.core.config.vertx.binder.rest.VertxRestApiConfigurer;
import realm.packages.vertx.core.config.vertx.exeception.VertxSpringCoreWaningException;
import realm.packages.vertx.core.config.vertx.exeception.http.ExceptionResolver;
import realm.packages.vertx.core.config.vertx.filter.VertxFilterStrategy;
import realm.packages.vertx.core.config.vertx.handler.http.ControllerBeforeResolver;
import realm.packages.vertx.core.config.vertx.handler.http.ControllerResultResolver;
import realm.packages.vertx.core.config.vertx.security.VertxSecurityProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static org.springframework.core.annotation.AnnotatedElementUtils.isAnnotated;

@Component
@Getter
public class VertxRoutingCenterBinder {

    private final Map<String, Method> controllerRequestMapper = new HashMap<>();

    @Autowired private ApplicationContext context;
    @Autowired private ParamExtractor paramExtractor;
    @Autowired private PathConverter pathConverter;
    @Autowired private ApplicationConfigModel applicationConfigModel;
    @Autowired private VertxFilterStrategy filterResolver;
    @Autowired private ApplicationContext applicationContext;
    @Autowired private VertxRestApi vertxRestApi;
    @Autowired private VertxSecurityProvider securityProvider;
    @Autowired private List<ControllerBeforeResolver> beforeResolvers;

    private Set<BeanDefinition> controllers;
    private Map<String, List<Pair<Method, Object>>> afterControllerMapping;
    private Map<String, List<Pair<Method, Object>>> beforeControllerMapping;

    @PostConstruct
    public void initRestResolvers() {
        try {
            VertxRestApiConfigurer externalConfig = applicationContext.getBean(VertxRestApiConfigurer.class);
            if (externalConfig != null) {
                externalConfig.config(vertxRestApi);
            }

            if(controllers == null){
                controllers = new HashSet<>();
            }
        } catch (NoSuchBeanDefinitionException ignored) {
        }
    }

    public void bind(Router router) throws Exception {
        if (router == null) return;
        beforeControllerMapping = scanBefore();
        afterControllerMapping = scanAfter();

        new RequestFilter(context, vertxRestApi.getFilterExceptionResolver())
                .bindFilters(router);

        bindController(router, VertxRestController.class,
                vertxRestApi.getControllerResultResolver(), vertxRestApi.getExceptionResolver());
        bindController(router, VertxWebController.class,
                vertxRestApi.getWebControllerResultResolver(), vertxRestApi.getWebExceptionResolver());

        detectDuplicatePath();
    }

    private void bindController(
            Router router, Class<? extends Annotation> annotationType,
            ControllerResultResolver controllerResultResolver, ExceptionResolver exceptionResolver) {

        Set<BeanDefinition> controllers = scanByAnnotationType(annotationType);
        if (this.controllers == null) this.controllers = new HashSet<>();

        this.controllers.addAll(controllers);

        new RequestHandler(
                context, router, controllers, beforeControllerMapping, afterControllerMapping,
                beforeResolvers, controllerResultResolver, exceptionResolver)
                .bind();
    }


    private void detectDuplicatePath() throws Exception {
        Set<String> requestMappings = new HashSet<>();

        for (BeanDefinition beanDef : controllers) {
            Class<?> beanClass = Class.forName(beanDef.getBeanClassName());

            for (Method method : beanClass.getMethods()) {
                if (!isAnnotated(method, VertxRequestMapping.class)) {
                    continue;
                }
                VertxRequestMapping requestMapping = AnnotatedElementUtils.getMergedAnnotation(method, VertxRequestMapping.class);
                String path = pathConverter.convertPathIdentity(requestMapping);
                if (requestMappings.contains(path)) {
                    throw new VertxSpringCoreWaningException("Duplicate path " + path, HttpStatus.EXPECTATION_FAILED.value());
                }
                requestMappings.add(path);
            }
        }
    }

    private Set<BeanDefinition> scanByAnnotationType(Class<? extends Annotation> annotationType) {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathBeanDefinitionScanner((GenericApplicationContext) context, false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(annotationType));

        return scanner.findCandidateComponents(applicationConfigModel.getBasePackage());
    }


    private Map<String, List<Pair<Method, Object>>> scanAfter() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(VertxAfterHandler.class));

        return scanner.findCandidateComponents(applicationConfigModel.getBasePackage())
                .stream()
                .map(this::extractClass)
                .filter(Objects::nonNull)
                .flatMap(beanClass -> {
                    Object beanObj = context.getBean(beanClass);
                    return Stream.of(beanClass.getMethods()).map(method -> Pair.of(method, beanObj));
                })
                .filter(pair -> AnnotationUtils.findAnnotation(pair.getLeft(), VertxAfterMapping.class) != null)
                .flatMap(pair -> {
                    // get annotation instance of method
                    VertxAfterMapping anno = AnnotationUtils.findAnnotation(pair.getLeft(), VertxAfterMapping.class);
                    // mapping annotation's value: request string (eg.. GET + /) -> after method
                    return Stream.of(anno.value()).map(requestId -> Pair.of(requestId, pair));
                })
                .collect(groupingBy(Pair::getLeft, mapping(Pair::getRight, toList()))); // group to a map
    }

    private Map<String, List<Pair<Method, Object>>> scanBefore() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(VertxBeforeHandler.class));

        return scanner.findCandidateComponents(applicationConfigModel.getBasePackage())
                .stream()
                .map(this::extractClass)
                .filter(Objects::nonNull)
                .flatMap(beanClass -> {
                    Object beanObj = context.getBean(beanClass);
                    return Stream.of(beanClass.getMethods()).map(method -> Pair.of(method, beanObj));
                })
                .filter(pair -> AnnotationUtils.findAnnotation(pair.getLeft(), VertxBeforeMapping.class) != null)
                .flatMap(pair -> {
                    // get annotation instance of method
                    VertxBeforeMapping anno = AnnotationUtils.findAnnotation(pair.getLeft(), VertxBeforeMapping.class);
                    // mapping annotation's value: request string (eg.. GET + /) -> before method
                    return Stream.of(anno.value()).map(requestId -> Pair.of(requestId, pair));
                })
                .collect(groupingBy(Pair::getLeft, mapping(Pair::getRight, toList()))); // group to a map
    }


    private Class<?> extractClass(BeanDefinition beanDefinition) {
        try {
            return Class.forName(beanDefinition.getBeanClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
