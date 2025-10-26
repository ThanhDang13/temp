package realm.packages.vertx.core.config.vertx.filter.security.jwt;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Data;

/** Thông tin cơ bản */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleSecurityUser {

  private Integer id;
  private String username;
  private String password;
  private String name;
  private List<String> roles;
  private Integer roleId;

  private Integer bsnId;
}
