package io.github.syphen.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.github.syphen.exception.DataTransformationException;
import io.github.syphen.exception.ErrorCode;
import io.github.syphen.operator.BaseOperator;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OperatorRegistryTest {

  private BaseOperator mockOperator;
  private OperatorRegistry registry;

  @BeforeEach
  void setUp() {
    mockOperator = mock(BaseOperator.class);
    registry = new OperatorRegistry(Map.of("UPPERCASE", mockOperator));
  }

  @Test
  void testGetOperator_validKey_returnsOperator() {
    BaseOperator result = registry.getOperator("UPPERCASE");
    assertSame(mockOperator, result);
  }

  @Test
  void testGetOperator_nullKey_throwsException() {
    DataTransformationException ex = assertThrows(
        DataTransformationException.class,
        () -> registry.getOperator(null)
    );
    assertEquals(ErrorCode.OPERATOR_KEY_NULL_OR_EMPTY_ERROR.name(), ex.getCode());
    assertTrue(ex.getCause().getMessage().contains("Operator type cannot be null or empty"));
  }

  @Test
  void testGetOperator_unknownKey_throwsException() {
    DataTransformationException ex = assertThrows(
        DataTransformationException.class,
        () -> registry.getOperator("TRIM")
    );
    assertEquals(ErrorCode.OPERATOR_NULL_ERROR.name(), ex.getCode());
    assertTrue(ex.getCause().getMessage().contains("Unknown operator type=TRIM"));
  }
}
