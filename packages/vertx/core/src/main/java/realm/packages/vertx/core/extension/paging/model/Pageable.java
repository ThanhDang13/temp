package realm.packages.vertx.core.extension.paging.model;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Pageable {
  public static final Integer DEFAULT_LIMIT = 10;
  public static final Integer DEFAULT_PAGE = 1;
  public static final Integer MAXIMUM_LIMIT = 200;

  private List<Order> sort;
  private Integer page;
  private Integer offset;
  private Integer limit;
  private Long total;
  private Map<String, String> params;

  public Pageable() {
    page = DEFAULT_PAGE;
    limit = DEFAULT_LIMIT;
    offset = 0;
  }

  public Integer getOffset() {
    if (offset == null || offset < 0) {
      return (page - 1) * limit < 0 ? 0 : (page - 1) * limit;
    }
    return offset < 0 ? 0 : offset;
  }
}
