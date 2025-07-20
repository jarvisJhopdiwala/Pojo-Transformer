package io.github.syphen.factory;

import io.github.syphen.exception.DataTransformationException;
import io.github.syphen.exception.ErrorCode;
import io.github.syphen.operator.BaseOperator;
import java.util.Map;
import java.util.Optional;

/**
 * Registry for managing and retrieving transformation operators by key.
 * <p>
 * This class provides a safe and centralized way to look up {@link BaseOperator} instances
 * using operator type keys. It throws descriptive exceptions when an operator is not found.
 * </p>
 */
public class OperatorRegistry {

  /** Immutable map of registered operator type keys to operator implementations. */
  private final Map<String, BaseOperator> operatorMap;

  /**
   * Constructs a new OperatorRegistry with a fixed set of operators.
   *
   * @param registeredOperators A map of operator type keys to their corresponding implementations.
   * @throws IllegalArgumentException if the provided map is null or empty.
   */
  public OperatorRegistry(Map<String, BaseOperator> registeredOperators) {
    this.operatorMap = Map.copyOf(registeredOperators);
  }

  /**
   * Retrieves a {@link BaseOperator} by its type key.
   *
   * @param key The operator type identifier (e.g., "UPPERCASE", "TRIM").
   * @return The corresponding operator implementation.
   * @throws DataTransformationException if the key is null/empty or the operator is not registered.
   */
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
