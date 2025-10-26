package realm.packages.vertx.core.config.vertx.binder.param.data;

import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class JsonObjectBody {
  private JsonObject body;
}
