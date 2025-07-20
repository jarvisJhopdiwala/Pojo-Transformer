package io.github.syphen.engine.strategy.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.syphen.engine.delegate.NodeTransformer;
import io.github.syphen.model.FieldTypeDescriptor;
import io.github.syphen.model.NodeContext;
import io.github.syphen.model.TransformationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NestedNodeTransformationStrategyTest {

  private NodeTransformer mockTransformer;
  private NestedNodeTransformationStrategy strategy;

  @BeforeEach
  void setUp() {
    mockTransformer = mock(NodeTransformer.class);
    strategy = new NestedNodeTransformationStrategy(mockTransformer);
  }

  @Test
  void testProcess_invokesNodeTransformerCorrectly() {
    // Arrange
    ObjectNode parentNode = JsonNodeFactory.instance.objectNode();
    JsonNode childNode = JsonNodeFactory.instance.objectNode(); // simulate nested object
    String fieldName = "address";

    parentNode.set(fieldName, childNode);

    NodeContext nodeContext = NodeContext.builder()
        .fieldName(fieldName)
        .build();

    TransformationContext transformationContext = TransformationContext.builder()
        .nodeContext(nodeContext)
        .build();

    Class<?> fieldClass = String.class;
    FieldTypeDescriptor descriptor = FieldTypeDescriptor.builder()
        .clazz(fieldClass)
        .build();

    // Act
    strategy.process(parentNode, transformationContext, descriptor);

    // Assert
    verify(mockTransformer).applyTransformations(childNode, fieldClass, transformationContext);
  }
}
