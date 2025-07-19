package io.github.syphen.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements DataTransformationErrorCode {

  FIELD_NOT_EXIST(500),
  INVALID_INPUT_ERROR(400),
  OPERATOR_KEY_NULL_OR_EMPTY_ERROR(400),
  OPERATOR_NULL_ERROR(400),
  WRONG_ANNOTATION_ERROR(400),
  UNKNOWN_OPERATOR_TYPE_ERROR(400),
  TRANSFORMATION_ERROR(500),
  SERIALIZATION_ERROR(500),
  DESERIALIZATION_ERROR(500),
  DUPLICATE_OPERATOR_KEY_ERROR(500),
  INITIALIZATION_OPERATOR_ERROR(500);
  final int status;
}
