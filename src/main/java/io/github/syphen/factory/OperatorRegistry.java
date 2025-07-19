package io.github.syphen.factory;

import io.github.syphen.exception.DataTransformationException;
import io.github.syphen.exception.ErrorCode;
import io.github.syphen.operator.BaseOperator;
import java.util.Map;
import java.util.Optional;

public class OperatorRegistry {

  private final Map<String, BaseOperator> operatorMap;

  public OperatorRegistry(Map<String, BaseOperator> registeredOperators) {
    this.operatorMap = Map.copyOf(registeredOperators);
  }

  public BaseOperator getOperator(String key) {
    if (key == null) {
      throw DataTransformationException.propagate(ErrorCode.OPERATOR_KEY_NULL_OR_EMPTY_ERROR,
          new Throwable("Operator type cannot be null or empty"));
    }
    return Optional.ofNullable(operatorMap.get(key)).orElseThrow(
        () -> DataTransformationException.propagate(ErrorCode.OPERATOR_NULL_ERROR,
            new Throwable("Unknown operator type=" + key)));
  }
}
