package realm.packages.vertx.core.config.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import realm.packages.vertx.core.config.vertx.exeception.VertxSpringCoreException;

import java.io.IOException;

@Component
public class DefaultJackson {

    public static final PropertyNamingStrategy.SnakeCaseStrategy SNAKE_CASE = new PropertyNamingStrategy.SnakeCaseStrategy();
    private static ObjectMapper objectMapper;

    @Autowired
    private MappingJackson2HttpMessageConverter springMvcJacksonConverter;

    @PostConstruct
    public void init() {
        objectMapper = springMvcJacksonConverter.getObjectMapper();
    }

    public static ObjectMapper objectMapper() {
        return objectMapper;
    }

    public static String writeValueDfNull(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static String writePrettyValueDfNull(Object object) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static <T> T readObjectDfNull(String body, Class<T> clazz) {
        try {
            return objectMapper.readValue(body, clazz);
        } catch (IOException e) {
            return null;
        }
    }

    public static <T> T readValueDefaultNull(String json, TypeReference<T> type) {
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, type);
        } catch (IOException e) {
            return null;
        }
    }

    public static <T> T readValueDefaultNull(String json, JavaType type) {
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, type);
        } catch (IOException e) {
            return null;
        }
    }

    public static Object readObjectDfNull(ObjectMapper objectMapper, String body, Class clazz) throws VertxSpringCoreException {
        try {
            return objectMapper.readValue(body, clazz);
        } catch (IOException e) {
            throw new VertxSpringCoreException("Input wrong", HttpStatus.BAD_REQUEST.value());
        }
    }
}
