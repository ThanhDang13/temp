package realm.packages.vertx.core.extension.paging.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Order {
    private String property;
    private Direction direction;

    public Order() {
    }

    public Order(String property, Direction direction) {
        Order.this.property = property;
        Order.this.direction = direction;
    }


    public enum Direction {
        ASC, DESC;
    }
}
