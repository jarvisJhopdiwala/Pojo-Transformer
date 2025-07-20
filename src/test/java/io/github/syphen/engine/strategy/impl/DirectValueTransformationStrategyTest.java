package io.github.syphen.engine.strategy.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.syphen.factory.OperatorRegistry;
import io.github.syphen.model.FieldTypeDescriptor;
import io.github.syphen.model.NodeContext;
import io.github.syphen.model.TransformationContext;
import io.github.syphen.operator.BaseOperator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DirectValueTransformationStrategyTest {

  private OperatorRegistry operatorRegistry;
  private BaseOperator operator;
  private DirectValueTransformationStrategy strategy;
  private ObjectNode parentNode;

  @BeforeEach
  void setUp() {
    operatorRegistry = mock(OperatorRegistry.class);
    operator = mock(BaseOperator.class);
    strategy = new DirectValueTransformationStrategy(operatorRegistry);
    parentNode = JsonNodeFactory.instance.objectNode();
  }

  @Test
  void testProcess_singleValueField_transformsCorrectly() {
    // Given
    String fieldName = "name";
    JsonNode inputValue = JsonNodeFactory.instance.textNode("raw");
    JsonNode transformedValue = JsonNodeFactory.instance.textNode("transformed");

    parentNode.set(fieldName, inputValue);

    TransformationContext context = TransformationContext.builder()
        .nodeContext(NodeContext.builder().fieldName(fieldName).operateType("UPPER").build())
        .defaultOperatorType("LOWER")
        .build();

    FieldTypeDescriptor descriptor = FieldTypeDescriptor.builder()
        .clazz(String.class)
        .build();

    when(operatorRegistry.getOperator("UPPER")).thenReturn(operator);
    when(operator.apply(inputValue, String.class)).thenReturn(transformedValue);

    // When
    strategy.process(parentNode, context, descriptor);

    // Then
    assertEquals(transformedValue, parentNode.get(fieldName));
    verify(operator).apply(inputValue, String.class);
  }

  @Test
  void testProcess_arrayField_transformsEachElement() {
    String fieldName = "tags";
    ArrayNode array = JsonNodeFactory.instance.arrayNode()
        .add("one")
        .add("two");

    JsonNode transformed1 = JsonNodeFactory.instance.textNode("ONE");
    JsonNode transformed2 = JsonNodeFactory.instance.textNode("TWO");

    parentNode.set(fieldName, array);

    TransformationContext context = TransformationContext.builder()
        .nodeContext(NodeContext.builder().fieldName(fieldName).operateType("UPPER").build())
        .defaultOperatorType("LOWER")
        .build();

    FieldTypeDescriptor descriptor = FieldTypeDescriptor.builder()
        .clazz(List.class)
        .genericType(String.class)
        .build();

    JsonNode element1 = array.get(0);
    JsonNode element2 = array.get(1);

    when(operatorRegistry.getOperator("UPPER")).thenReturn(operator);
    when(operator.apply(element1, String.class)).thenReturn(transformed1);
    when(operator.apply(element2, String.class)).thenReturn(transformed2);

    strategy.process(parentNode, context, descriptor);

    assertEquals(transformed1, array.get(0));
    assertEquals(transformed2, array.get(1));

    verify(operator).apply(element1, String.class);
    verify(operator).apply(element2, String.class);
  }


  @Test
  void testProcess_arrayField_withNonArrayValue_doesNothing() {
    // Given
    String fieldName = "invalidArray";
    parentNode.set(fieldName, JsonNodeFactory.instance.textNode("not-an-array"));

    TransformationContext context = TransformationContext.builder()
        .nodeContext(NodeContext.builder().fieldName(fieldName).operateType("OP").build())
        .defaultOperatorType("DEFAULT")
        .build();

    FieldTypeDescriptor descriptor = FieldTypeDescriptor.builder()
        .clazz(List.class)
        .genericType(String.class)
        .build();

    when(operatorRegistry.getOperator("OP")).thenReturn(operator);

    // When
    strategy.process(parentNode, context, descriptor);

    // Then
    verify(operator, never()).apply(any(), any());
  }
}
