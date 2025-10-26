package realm.packages.vertx.core.extension.functional;

@FunctionalInterface
public interface SupplierThrowable<T> {

  T get() throws Exception;
}
