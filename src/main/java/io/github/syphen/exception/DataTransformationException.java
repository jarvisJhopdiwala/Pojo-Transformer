package io.github.syphen.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DataTransformationException extends RuntimeException {

  private final int status;
  private final String code;
  private final transient Map<String, Object> context;

  @Builder
  private DataTransformationException(DataTransformationErrorCode errorCode,
      Map<String, Object> context) {
    super();
    this.code = errorCode.name();
    this.status = errorCode.getStatus();
    this.context = context;
  }

  private DataTransformationException(Throwable cause, DataTransformationErrorCode errorCode) {
    super(cause);
    this.code = errorCode.name();
    this.status = errorCode.getStatus();
    this.context = cause != null && cause.getLocalizedMessage() != null ?
        Map.of("message", cause.getLocalizedMessage()) : new HashMap<>();
  }

  public static DataTransformationException error(DataTransformationErrorCode errorCode) {
    return new DataTransformationException(errorCode, new HashMap<>());
  }

  public static DataTransformationException propagate(DataTransformationErrorCode errorCode,
      Throwable t) {
    return new DataTransformationException(t, errorCode);
  }

  public static DataTransformationException error(DataTransformationErrorCode errorCode,
      Map<String, Object> context) {
    return new DataTransformationException(errorCode, context);
  }
}
