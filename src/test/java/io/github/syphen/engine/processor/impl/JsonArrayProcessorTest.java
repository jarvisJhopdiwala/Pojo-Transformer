package io.github.syphen.engine.processor.impl;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.syphen.engine.delegate.NodeTransformer;
import io.github.syphen.model.TransformationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JsonArrayProcessorTest {

  private NodeTransformer nodeTransformer;
  private JsonArrayProcessor jsonArrayProcessor;
  private TransformationContext transformationContext;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    nodeTransformer = mock(NodeTransformer.class);
    transformationContext = mock(TransformationContext.class);
    jsonArrayProcessor = new JsonArrayProcessor(nodeTransformer);
    objectMapper = new ObjectMapper();
  }

  @Test
  void testTransform_withValidObjectNodes_shouldApplyTransformation() {
    // Given
    ArrayNode arrayNode = objectMapper.createArrayNode();
    ObjectNode object1 = objectMapper.createObjectNode();
    ObjectNode object2 = objectMapper.createObjectNode();
    arrayNode.add(object1);
    arrayNode.add(object2);

    // When
    jsonArrayProcessor.transform(arrayNode, DummyPojo.class, transformationContext);

    // Then
    verify(nodeTransformer, times(2))
        .applyTransformations(any(JsonNode.class), eq(DummyPojo.class), eq(transformationContext));
  }

  @Test
  void testTransform_withNullAndNonObjectNodes_shouldSkipThem() {
    // Given
    ArrayNode arrayNode = objectMapper.createArrayNode();
    arrayNode.addNull();                            // null element
    arrayNode.add(objectMapper.getNodeFactory().textNode("string"));  // non-object
    arrayNode.add(objectMapper.createObjectNode()); // valid object

    // When
    jsonArrayProcessor.transform(arrayNode, DummyPojo.class, transformationContext);

    // Then: only one valid transformation
    verify(nodeTransformer, times(1))
        .applyTransformations(any(JsonNode.class), eq(DummyPojo.class), eq(transformationContext));
  }

  // Dummy POJO class for the test
  static class DummyPojo {

    public String name;
  }
}
