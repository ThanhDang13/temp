package realm.packages.vertx.core.config.vertx.binder.param.extractor;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava3.core.MultiMap;
import io.vertx.rxjava3.core.http.HttpServerRequest;
import io.vertx.rxjava3.ext.web.FileUpload;
import io.vertx.rxjava3.ext.web.RoutingContext;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import realm.packages.vertx.core.annotation.http.*;
import realm.packages.vertx.core.config.jackson.DefaultJackson;
import realm.packages.vertx.core.config.vertx.binder.param.converter.ParamConverter;
import realm.packages.vertx.core.config.vertx.binder.param.converter.factory.ParamConverterFactory;
import realm.packages.vertx.core.config.vertx.binder.param.converter.type.VertxFileUpload;
import realm.packages.vertx.core.config.vertx.binder.param.data.JsonObjectBody;
import realm.packages.vertx.core.config.vertx.binder.param.parser.ControllerParamParser;
import realm.packages.vertx.core.config.vertx.binder.param.standardizer.ParamStandardizer;
import realm.packages.vertx.core.config.vertx.exception.VertxSpringCoreException;
import realm.packages.vertx.core.config.vertx.security.model.principal.VertxPrincipal;
import realm.packages.vertx.core.config.vertx.security.model.principal.impl.VertxUser;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

@Component
public class ParamExtractor {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ParamConverterFactory paramConverterFactory;
    @Autowired
    private List<ControllerParamParser> controllerParamParsers;
    @Autowired
    private ParamStandardizer paramStandardizer;

    private Map<Class, ControllerParamParser> paramParserMap;

    @PostConstruct
    public void init() {
        paramParserMap = controllerParamParsers.stream().collect(toMap(ControllerParamParser::type, Function.identity()));
    }

    public Object[] extractArguments(Method method, HttpServerRequest request, String body, RoutingContext routingContext)
            throws VertxSpringCoreException {

        Parameter[] params = method.getParameters();
        int argsSize = params.length;
        final Object[] values = new Object[argsSize];

        for (int i = 0; i < argsSize; i++) {
            Parameter param = params[i];
            if (param.isAnnotationPresent(VertxRequestParam.class)) {
                VertxRequestParam paramAnnotation = param.getAnnotation(VertxRequestParam.class);
                String defaultValue = paramAnnotation.defaultValue();
                String paramName = paramAnnotation.value();
                values[i] = getRequestParamValue(request, routingContext.fileUploads(), param, paramName, defaultValue);
                if (paramAnnotation.required() && values[i] == null)
                    throw new VertxSpringCoreException("param " + paramName + " should not be null", NOT_ACCEPTABLE.value());
            } else if (param.isAnnotationPresent(VertxParamModel.class)) {
                values[i] = extractModelAttitude(request.params(), param);
            } else if (param.isAnnotationPresent(VertxModelAttitude.class)) {
                values[i] = extractModelAttitude(request.formAttributes(), param);
            } else if (param.isAnnotationPresent(VertxPathVariable.class)) {
                VertxPathVariable pathVariable = param.getAnnotation(VertxPathVariable.class);
                values[i] = convertPathVariable(request, param, pathVariable.value());
                if (values[i] == null)
                    throw new VertxSpringCoreException("param " + pathVariable.value() + " should not be null", NOT_ACCEPTABLE.value());
            } else if (param.isAnnotationPresent(VertxRequestBody.class)) {
                try {
                    values[i] = extractRequestBody(body, param);
                } catch (VertxSpringCoreException e) {
                    if (!param.getAnnotation(VertxRequestBody.class).required()) values[i] = null;
                    else throw e;
                }
            } else if (param.isAnnotationPresent(VertxNewInstance.class)) {
                try {
                    values[i] = extractNewInstance(param);
                } catch (VertxSpringCoreException e) {
                    if (!param.getAnnotation(VertxRequestBody.class).required()) values[i] = null;
                    else throw e;
                }
            } else if (param.getType().equals(JsonObjectBody.class)) {
                try {
                    values[i] = new JsonObjectBody().setBody(new JsonObject(body));
                } catch (Exception e) {
                    throw e;
                }
            } else if (param.getType().equals(RoutingContext.class)) {
                values[i] = routingContext;
            } else if (param.getType().equals(VertxPrincipal.class)) {
                values[i] = extractPrincipal(routingContext);
            } else if (paramParserMap.containsKey(param.getType())) {
                values[i] = paramParserMap.get(param.getType()).parser(request);
            } else {
                throw new VertxSpringCoreException("cannot bind args values, considering annotation missing or wrong");
            }
        }

        return values;
    }

    private Object extractRequestBody(String body, Parameter param) throws VertxSpringCoreException {

        Type type = param.getAnnotatedType().getType();
        if (type instanceof Class<?>) {
            Class<?> clazz = param.getType();
            return DefaultJackson.readObjectDfNull(objectMapper, body, clazz);
        } else if (type instanceof ParameterizedType) {
            Class<?> clazz = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
            try {
                return objectMapper.readValue(body, objectMapper.getTypeFactory()
                        .constructCollectionType((Class<? extends Collection>) param.getType(), clazz));
            } catch (IOException e) {
                throw new VertxSpringCoreException("input wrong", HttpStatus.BAD_REQUEST.value());
            }
        } else {
            throw new VertxSpringCoreException("cannot bind args values");
        }
    }

    private Object extractNewInstance(Parameter param) throws VertxSpringCoreException {
        try {
            Type type = param.getAnnotatedType().getType();
            if (type instanceof Class<?>) {
                Class<?> clazz = param.getType();
                return clazz.newInstance();
            } else if (type instanceof ParameterizedType) {
                Class<?> clazz = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
                return clazz.newInstance();
            } else {
                throw new VertxSpringCoreException("cannot bind args values");
            }
        } catch (Exception e) {
            throw new VertxSpringCoreException("cannot bind args values");
        }
    }

    private Object extractModelAttitude(MultiMap attitudes, Parameter param) {

        Map<String, Object> attMap = new HashMap<>();
        for (String attName : attitudes.names()) {
            attMap.put(attName, attitudes.get(attName));
        }
        try {
            return objectMapper.convertValue(attMap, param.getType());
        } catch (Exception e) {
            return null;
        }
    }


    private VertxPrincipal extractPrincipal(RoutingContext routingContext) {
        if (routingContext.user() == null)
            return null;

        VertxUser vertxUser = (VertxUser) routingContext.user().getDelegate();
        return Optional.ofNullable(vertxUser).map(VertxUser::getPrincipal).orElse(null);
    }

    private Object getRequestParamValue(HttpServerRequest request, List<FileUpload> fileUploads, Parameter param, String paramName, String defaultValue) throws VertxSpringCoreException {

        Optional<FileUpload> fileUpload = fileUploads.stream()
                .filter(file -> file.getDelegate().name().equals(paramName)).findFirst();
        ParamConverter paramConverter = paramConverterFactory.getParamConverter(param.getType());

        if (fileUpload.isPresent() && param.getType().equals(VertxFileUpload.class)) {
            return paramConverter.convert(fileUpload.get());
        } else {
            String paramValue = paramStandardizer.standardize(paramName, request.getParam(paramName));
            if (paramValue == null) {
                paramValue = paramStandardizer.standardize(paramName, request.getFormAttribute(paramName));
            }
            if (paramValue != null) {
                return paramConverter.convert(paramValue);
            } else if (isNotEmpty(defaultValue)) {
                return paramConverter.convert(defaultValue);
            } else {
                return paramConverter.convertNullValue();
            }
        }
    }

    private Object convertPathVariable(HttpServerRequest request, Parameter param, String paramName) throws VertxSpringCoreException {
        String paramValue = paramStandardizer.standardize(paramName, request.getParam(paramName));
        if (paramValue != null) {
            ParamConverter paramConverter = paramConverterFactory.getParamConverter(param.getType());
            return paramConverter.convert(paramValue);
        } else {
            return null;
        }
    }
}
