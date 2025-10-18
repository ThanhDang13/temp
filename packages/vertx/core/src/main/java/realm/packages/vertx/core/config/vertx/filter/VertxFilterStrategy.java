package realm.packages.vertx.core.config.vertx.filter;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class VertxFilterStrategy {

    @Autowired private ApplicationContext context;
    private List<VertxGenericFilter> vertxGenericFilters;

    @PostConstruct
    public void init() {
        Map<String, VertxGenericFilter> map = context.getBeansOfType(VertxGenericFilter.class);
        vertxGenericFilters = new ArrayList<>(map.values());
    }

    public void setVertxGenericFilters(List<VertxGenericFilter> vertxGenericFilters) {
        this.vertxGenericFilters = vertxGenericFilters;
    }

    public List<VertxGenericFilter> getVertxGenericFilters() {
        return vertxGenericFilters;
    }
}
