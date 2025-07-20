package io.github.syphen.engine.processor;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.syphen.engine.strategy.FieldTransformationStrategy;
import io.github.syphen.factory.StrategySelector;
import io.github.syphen.model.FieldTypeDescriptor;
import io.github.syphen.model.NodeContext;
import io.github.syphen.model.TransformationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FieldProcessorTest {

  private StrategySelector strategySelector;
  private FieldTransformationStrategy strategy;
  private FieldProcessor fieldProcessor;

  private ObjectNode parentNode;
  private FieldTypeDescriptor descriptor;

  @BeforeEach
  void setup() {
    strategySelector = mock(StrategySelector.class);
    strategy = mock(FieldTransformationStrategy.class);
    fieldProcessor = new FieldProcessor(strategySelector);
    parentNode = JsonNodeFactory.instance.objectNode();
    descriptor = FieldTypeDescriptor.builder().build();
  }

  @Test
  void testProcessField_appliesStrategy_whenValidContext() {
    // Given
    TransformationContext context = TransformationContext.builder()
        .nodeContext(NodeContext.builder().fieldName("test").build())
        .build();

    when(strategySelector.determineStrategy(context)).thenReturn(strategy);

    // When
    fieldProcessor.processField(parentNode, context, descriptor);

    // Then
    verify(strategy).process(parentNode, context, descriptor);
  }

  @Test
  void testProcessField_skips_whenInvalidContext() {
    // Given: fieldContext has null nodeContext
    TransformationContext context = TransformationContext.builder().nodeContext(null).build();

    // When
    fieldProcessor.processField(parentNode, context, descriptor);

    // Then: selector and strategy should not be called
    verifyNoInteractions(strategySelector);
    verifyNoInteractions(strategy);
  }

  @Test
  void testProcessField_throws_whenStrategyIsNull() {
    // Given
    TransformationContext context = TransformationContext.builder()
        .nodeContext(NodeContext.builder().fieldName("something").build())
        .build();

    when(strategySelector.determineStrategy(context)).thenReturn(null);

    // When + Then
    assertThrows(NullPointerException.class, () ->
        fieldProcessor.processField(parentNode, context, descriptor));
  }

  @Test
  void testProcessField_throws_whenStrategySelectorIsNull() {
    // Given
    fieldProcessor = new FieldProcessor(null);

    TransformationContext context = TransformationContext.builder()
        .nodeContext(NodeContext.builder().fieldName("field").build())
        .build();

    // When + Then
    assertThrows(NullPointerException.class, () ->
        fieldProcessor.processField(parentNode, context, descriptor));
  }
}
