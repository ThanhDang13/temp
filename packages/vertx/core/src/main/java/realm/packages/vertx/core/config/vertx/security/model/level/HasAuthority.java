package realm.packages.vertx.core.config.vertx.security.model.level;

import io.vertx.rxjava3.ext.auth.User;
import java.util.Optional;
import lombok.Data;
import realm.packages.vertx.core.config.vertx.security.model.principal.VertxAuthentication;
import realm.packages.vertx.core.config.vertx.security.model.principal.VertxPrincipal;
import realm.packages.vertx.core.config.vertx.security.model.principal.impl.VertxUser;

@Data
public class HasAuthority implements SecurityLevel {

  /** Quyền đơn */
  private String authority;

  /** Bất kì quyền nào cũng đều được chấp thuận */
  private String[] anyAuthority;

  public HasAuthority(String authority) {
    this.authority = authority;
  }

  public HasAuthority(String... anyAuthority) {
    this.anyAuthority = anyAuthority;
  }

  @Override
  public boolean isPermitted(User user) {

    return Optional.ofNullable((VertxUser) user.getDelegate())
        .map(VertxUser::getPrincipal)
        .map(VertxPrincipal::getAuthentication)
        .map(VertxAuthentication::getAuthorities)
        .map(
            list -> {
              if (authority != null) {
                return list.contains(authority);
              }

              if (anyAuthority == null || anyAuthority.length == 0) {
                return false;
              }

              Boolean result = false;
              for (String role : anyAuthority) {
                if (list.contains(role)) {
                  result = true;
                  break;
                }
              }
              return result;
            })
        .orElse(false);
  }
}
