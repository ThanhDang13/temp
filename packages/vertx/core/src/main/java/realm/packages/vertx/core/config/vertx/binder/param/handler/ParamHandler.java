package realm.packages.vertx.core.config.vertx.binder.param.handler;

import io.vertx.core.Future;
import io.vertx.core.http.HttpHeaders;
import io.vertx.rxjava3.core.Promise;
import io.vertx.rxjava3.core.buffer.Buffer;
import io.vertx.rxjava3.core.file.FileSystem;
import io.vertx.rxjava3.core.http.HttpServerRequest;
import io.vertx.rxjava3.ext.web.FileUpload;
import io.vertx.rxjava3.ext.web.RoutingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import realm.packages.vertx.core.config.vertx.binder.param.config.UploadConfig;
import realm.packages.vertx.core.config.vertx.binder.param.extractor.ParamExtractor;

import java.lang.reflect.Method;
import java.util.List;

import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED;
import static io.netty.handler.codec.http.HttpHeaderValues.MULTIPART_FORM_DATA;

@Component
public class ParamHandler {

    @Autowired
    private UploadConfig uploadConfig;

    @Autowired
    private ParamExtractor paramExtractor;

    private void makeUploadDirBlocking(FileSystem fileSystem) {
        if (!fileSystem.existsBlocking(uploadConfig.getUploadDir())) {
            fileSystem.mkdirsBlocking(uploadConfig.getUploadDir());
        }
    }

    // TODO handler for no file upload -> pending request
    public Future<Object[]> handle(RoutingContext routingContext, Method method) {
        Promise<Object[]> promise = Promise.promise();
        HttpServerRequest request = routingContext.request();

        if (isUploadRequest(request)) {
            try {
                List<FileUpload> fileUploads = routingContext.fileUploads();
                if (!fileUploads.isEmpty()) {
                    for (FileUpload fu : fileUploads) {
                        System.out.println("Uploaded: " + fu.uploadedFileName());
                    }
                }

                promise.complete(paramExtractor.extractArguments(method, request, null, routingContext));
            } catch (Exception e) {
                promise.fail(e);
            }
        } else if (isFormSubmit(request)) {
            try {
                promise.complete(paramExtractor.extractArguments(method, request, null, routingContext));
            } catch (Exception e) {
                promise.fail(e);
            }
        } else {
            Buffer buff = routingContext.body().buffer();
            String body = buff != null ? buff.toString("UTF-8") : null;
            try {
                promise.complete(paramExtractor.extractArguments(method, request, body, routingContext));
            } catch (Exception e) {
                promise.fail(e);
            }
        }

        return promise.future();
    }

    private boolean isFormSubmit(HttpServerRequest request) {
        final String contentType = request.getHeader(HttpHeaders.CONTENT_TYPE.toString());
        if (contentType == null) return false;
        return contentType.toLowerCase().startsWith(APPLICATION_X_WWW_FORM_URLENCODED.toString());
    }

    private boolean isUploadRequest(HttpServerRequest request) {
        final String contentType = request.getHeader(HttpHeaders.CONTENT_TYPE.toString());
        if (contentType == null) return false;
        return contentType.toLowerCase().startsWith(MULTIPART_FORM_DATA.toString());
    }
}