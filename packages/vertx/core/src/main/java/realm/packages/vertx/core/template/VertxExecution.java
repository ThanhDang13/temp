package realm.packages.vertx.core.template;

import static java.util.Arrays.stream;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.rxjava3.core.eventbus.Message;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import realm.packages.vertx.core.config.vertx.VertxWrapper;
import realm.packages.vertx.core.extension.functional.Procedurable;
import realm.packages.vertx.core.extension.functional.SupplierThrowable;

public class VertxExecution {
  private static final Logger log = LoggerFactory.getLogger(VertxExecution.class);

  public static <T> Single<T> rxBlocking(SupplierThrowable<T> resultSupplier) {
    return VertxWrapper.vertx()
        .<T>rxExecuteBlocking(
            promise -> {
              try {
                promise.complete(resultSupplier.get());
              } catch (Exception e) {
                promise.fail(e);
              }
            })
        // ép Maybe -> Single, nếu empty thì throw NoSuchElementException
        .toSingle();
    // hoặc: .switchIfEmpty(Single.error(new NoSuchElementException("No value emitted")));
  }

  //    public static <T> Single<T> rxBlockingAsync(SupplierThrowable<T> resultSupplier) {
  //        return rxBlockingAsync(resultSupplier, null);
  //    }

  public static <T> Single<T> rxBlockingAsync(SupplierThrowable<T> resultSupplier, T defaultValue) {
    return VertxWrapper.vertx()
        .<T>rxExecuteBlocking(
            promise -> {
              try {
                T result = resultSupplier.get();
                if (result != null) {
                  promise.complete(result);
                } else {
                  log.warn("rxBlockingAsync returned null - completing without value");
                  promise.complete(defaultValue); // Maybe.empty()
                }
              } catch (Exception e) {
                promise.fail(e);
              }
            },
            false)
        .toSingle();
  }

  //    public static <T> Single<T> rxBlockingAsync(Procedurable procedurable) {
  //        return rxBlockingAsync(procedurable, null);
  //    }

  public static <T> Single<T> rxBlockingAsync(Procedurable procedurable, T defaultValue) {
    return VertxWrapper.vertx()
        .<T>rxExecuteBlocking(
            promise -> {
              try {
                procedurable.process();
                promise.complete(defaultValue);
              } catch (Exception e) {
                promise.fail(e);
              }
            },
            false)
        .toSingle();
  }

  public static <T> Observable<Message<T>> rxConsumer(String address) {
    return VertxWrapper.vertx().eventBus().<T>consumer(address).toObservable();
  }

  public static Single<Message<Object>> rxSend(String address, Object message) {
    return VertxWrapper.vertx()
        .eventBus()
        //                .rxSend(address, message);
        .rxRequest(address, message);
  }

  public static Single<Message<Object>> rxSend(
      String address, Object message, DeliveryOptions deliveryOptions) {
    return VertxWrapper.vertx()
        .eventBus()
        //                .rxSend(address, message, deliveryOptions);
        .rxRequest(address, message, deliveryOptions);
  }

  public static void rxSubscribeIndependentObservable(Single... singles) {
    List lstSingles = stream(singles).filter(Objects::nonNull).collect(Collectors.toList());
    Single.zip(lstSingles, result -> "Success")
        .subscribe(
            result -> log.info(String.valueOf(result)),
            throwable -> log.error(throwable.toString(), throwable));
  }
}
