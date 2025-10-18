package realm.packages.vertx.core.config.vertx.binder.param.converter.type;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.File;

@Getter
@Setter
@Accessors(chain = true)
public class VertxFileUpload {
    String fileName;
    File file;
    Integer employeeId;
}