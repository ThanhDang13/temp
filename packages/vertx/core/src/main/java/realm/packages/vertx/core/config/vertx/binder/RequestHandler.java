package realm.packages.vertx.core.config.vertx.binder;

import io.reactivex.rxjava3.core.Single;
import io.vertx.rxjava3.ext.web.Router;
import io.vertx.rxjava3.ext.web.RoutingContext;
import io.vertx.rxjava3.ext.web.handler.BodyHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.AntPathMatcher;
import realm.packages.vertx.core.annotation.http.request.mapping.VertxRequestMapping;
import realm.packages.vertx.core.config.vertx.binder.param.handler.ParamHandler;
import realm.packages.vertx.core.config.vertx.binder.path.PathConverter;
import realm.packages.vertx.core.config.vertx.exeception.VertxSpringCoreException;
import realm.packages.vertx.core.config.vertx.exeception.http.ExceptionResolver;
import realm.packages.vertx.core.config.vertx.handler.http.ControllerBeforeResolver;
import realm.packages.vertx.core.config.vertx.handler.http.ControllerIOWrapper;
import realm.packages.vertx.core.config.vertx.handler.http.ControllerResultResolver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Boolean.TRUE;

public class RequestHandler {

    private final Router router;
    private final Set<BeanDefinition> controllers;
    private final ExceptionResolver exceptionResolver;
    private final ControllerResultResolver controllerResultResolver;

    private final Map<String, List<Pair<Method, Object>>> afterControllerMapping;
    private final Map<String, List<Pair<Method, Object>>> beforeControllerMapping;

    private ApplicationContext context;
    private ParamHandler paramHandler;
    private PathConverter pathConverter;
    private List<ControllerBeforeResolver> beforeResolvers;
    private VertxRoutingCenterBinder routingCenterBinder;

    public RequestHandler(ApplicationContext context, Router router, Set<BeanDefinition> controllers,
                          Map<String, List<Pair<Method, Object>>> beforeControllerMapping,
                          Map<String, List<Pair<Method, Object>>> afterControllerMapping,
                          List<ControllerBeforeResolver> beforeResolvers,
                          ControllerResultResolver controllerResultResolver, ExceptionResolver exceptionResolver) {

        this.context = context;
        this.beforeResolvers = beforeResolvers;
        this.paramHandler = context.getBean(ParamHandler.class);
        this.pathConverter = context.getBean(PathConverter.class);

        this.router = router;
        this.controllers = controllers;
        this.beforeControllerMapping = beforeControllerMapping;
        this.afterControllerMapping = afterControllerMapping;
        this.controllerResultResolver = controllerResultResolver;
        this.exceptionResolver = exceptionResolver;

        this.routingCenterBinder = context.getBean(VertxRoutingCenterBinder.class);
    }

    //TODO refactor bind controllers
    public void bind() {
        Map<Class<?>, List<Method>> latControllersBind = new HashMap<>();
        for (BeanDefinition beanDef : controllers) {
            try {
                Class<?> beanClass = Class.forName(beanDef.getBeanClassName());
                Object beanObj = context.getBean(beanClass);
                List<Method> methodsBindLatest = new ArrayList<>();
                Stream.of(beanClass.getMethods())
                        .filter(method -> AnnotatedElementUtils.isAnnotated(method, VertxRequestMapping.class))
                        .forEach(method -> {
                            VertxRequestMapping requestMapping = AnnotatedElementUtils.getMergedAnnotation(method, VertxRequestMapping.class);
                            String path = pathConverter.convertPath(requestMapping.path());
                            if (path.contains("*")) {
                                methodsBindLatest.add(method);
                            } else {
                                bindControllerRouting(router, beanObj, method);
                            }
                        });
                latControllersBind.put(beanClass, methodsBindLatest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        latControllersBind.forEach((beanClass, methods) -> {
            Object beanObj = context.getBean(beanClass);
            methods.forEach(method -> bindControllerRouting(router, beanObj, method));
        });
    }

    private void bindControllerRouting(Router router, Object beanObj, Method method) {
        VertxRequestMapping requestMapping = AnnotatedElementUtils.getMergedAnnotation(method, VertxRequestMapping.class);
        String path = pathConverter.convertPath(requestMapping.path());

        routingCenterBinder.getControllerRequestMapper().put(path, method);

        router.route(requestMapping.method().getVertxMethod(), path)
                .handler(BodyHandler.create()
                        .setUploadsDirectory("uploads")
                        .setDeleteUploadedFilesOnEnd(false))
                .handler(routingContext -> {
                    // no need to response if filters already returned in case;
                    if (routingContext.response().closed()) return;
                    long startTime = System.currentTimeMillis();

                    paramHandler.handle(routingContext, method).onComplete(result -> {
                        if (result.succeeded()) {
                            try {
                                handlerRequest(routingContext, beanObj, method, result.result(), startTime);
                            } catch (Exception e) {
                                exceptionResolver.resolveException(routingContext, e);
                            }
                        } else {
                            exceptionResolver.resolveException(routingContext, result.cause());
                        }
                    });
                });
    }


    private List<Pair<Method, Object>> matchList(Map<String, List<Pair<Method, Object>>> controllerMapping, VertxRequestMapping requestMapping) {
        AntPathMatcher matcher = new AntPathMatcher();

        List<Pair<Method, Object>> mappedController = controllerMapping
                .keySet()
                .stream()
                .filter(requestPattern -> {
                    String method = requestPattern.substring(0, requestPattern.indexOf(":"));
                    String requestPathPattern = requestPattern.substring(requestPattern.indexOf(":") + 1, requestPattern.length());

                    return StringUtils.equals(method, requestMapping.method().name()) && matcher.match(requestPathPattern, requestMapping.path());
                })
                .flatMap(requestPattern -> controllerMapping.get(requestPattern).stream())
                .collect(Collectors.toList());

        return mappedController;
    }


    private void handlerRequest(RoutingContext routingContext,
                                Object beanObj,
                                Method method,
                                Object[] args,
                                Long startTime)
            throws IllegalAccessException, VertxSpringCoreException, InvocationTargetException {


        // gom các endpoint
        VertxRequestMapping requestMapping = AnnotatedElementUtils.getMergedAnnotation(method, VertxRequestMapping.class);
        List<Pair<Method, Object>> afterHandlers = matchList(afterControllerMapping, requestMapping);

        // Get proper before handlers of the request
        List<Pair<Method, Object>> beforeHandlers = matchList(beforeControllerMapping, requestMapping);

        // Separate param vs value - keep order of args
        Map<Parameter, Object> mappingParameterValue = new LinkedHashMap<>();
        Parameter[] params = method.getParameters();
        int argsSize = params.length;

        for (int i = 0; i < argsSize; i++) {
            mappingParameterValue.put(params[i], args[i]);
        }

        List<Single<Boolean>> controllerBeforeSingles = new ArrayList<>();
        for (ControllerBeforeResolver controllerBeforeResolver : beforeResolvers) {
            controllerBeforeSingles.add(controllerBeforeResolver.resolve(beforeHandlers, mappingParameterValue,
                    routingContext.request()).toSingleDefault(TRUE));
        }

        Single.zip(controllerBeforeSingles, dtos -> dtos)
                .subscribe(objects -> {
                    try {
                        Object returnValue = method.invoke(beanObj, args);
                        ControllerIOWrapper ioWrapper = new ControllerIOWrapper(args, returnValue, routingContext, afterHandlers);
                        ioWrapper.setPathPattern(requestMapping.path());
                        ioWrapper.setStartProcessTimestamp(startTime);

                        controllerResultResolver.resolve(ioWrapper);
                    } catch (Exception e) {
                        exceptionResolver.resolveException(routingContext, e);
                    }
                }, err -> exceptionResolver.resolveException(routingContext, err));
    }
}
