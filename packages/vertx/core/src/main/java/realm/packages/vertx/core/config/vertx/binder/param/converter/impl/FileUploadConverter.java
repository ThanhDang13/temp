package realm.packages.vertx.core.config.vertx.binder.param.converter.impl;

import io.vertx.rxjava3.ext.web.FileUpload;
import java.io.File;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import realm.packages.vertx.core.config.vertx.binder.param.converter.ParamConverter;
import realm.packages.vertx.core.config.vertx.binder.param.converter.type.VertxFileUpload;
import realm.packages.vertx.core.config.vertx.exception.VertxSpringCoreException;

@Component
public class FileUploadConverter extends ParamConverter<VertxFileUpload> {

  @Override
  public VertxFileUpload convert(Object value) throws VertxSpringCoreException {
    try {

      if (value instanceof FileUpload) {
        FileUpload fileUpload = ((FileUpload) value);
        return new VertxFileUpload()
            .setFile(new File(fileUpload.uploadedFileName()))
            .setFileName(fileUpload.fileName());
      } else {
        return null;
      }
    } catch (Exception e) {
      throw new VertxSpringCoreException(
          "Invalid number value: " + value, HttpStatus.BAD_REQUEST.value());
    }
  }

  @Override
  public Class<VertxFileUpload> getSupportType() {
    return VertxFileUpload.class;
  }
}
