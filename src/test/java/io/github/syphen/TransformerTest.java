package io.github.syphen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.syphen.engine.delegate.NodeTransformer;
import io.github.syphen.exception.DataTransformationException;
import io.github.syphen.model.NodeContext;
import io.github.syphen.model.TransformationContext;
import io.github.syphen.utils.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransformerTest {

  private NodeTransformer mockTransformer;
  private Transformer transformer;

  @BeforeEach
  void setUp() {
    mockTransformer = mock(NodeTransformer.class);
    transformer = new Transformer(mockTransformer);
  }

  @Test
  void transform_shouldReturnTransformedObject_whenValidInput() {
    SampleInput input = new SampleInput("John", 30);
    NodeContext nodeContext = NodeContext.builder().fieldName("name").build();
    TransformationContext ctx = TransformationContext.builder().nodeContext(nodeContext).build();

    // Spy on ObjectMapper to verify re-mapping
    ObjectMapper objectMapper = MapperUtil.getObjectMapper();

    SampleInput result = transformer.transform(input, ctx);

    assertNotNull(result);
    assertEquals(input.name, result.name);
    assertEquals(input.age, result.age);
    verify(mockTransformer).applyTransformations(any(ObjectNode.class), eq(SampleInput.class),
        eq(ctx));
  }

  @Test
  void buildTransformedJsonNode_shouldReturnTransformedNode_whenValidInput() {
    SampleInput input = new SampleInput("Alice", 25);
    NodeContext nodeContext = NodeContext.builder().fieldName("name").build();
    TransformationContext ctx = TransformationContext.builder().nodeContext(nodeContext).build();

    JsonNode node = transformer.buildTransformedJsonNode(input, ctx);

    assertNotNull(node);
    assertTrue(node.isObject());
    assertEquals("Alice", node.get("name").asText());
    verify(mockTransformer).applyTransformations(any(ObjectNode.class), eq(SampleInput.class),
        eq(ctx));
  }

  @Test
  void transform_shouldReturnInputAsIs_whenInputIsNull() {
    SampleInput result = transformer.transform(null, null);
    assertNull(result);
    verifyNoInteractions(mockTransformer);
  }

  @Test
  void buildTransformedJsonNode_shouldReturnNull_whenInputIsNull() {
    JsonNode result = transformer.buildTransformedJsonNode(null, null);
    assertNull(result);
    verifyNoInteractions(mockTransformer);
  }

  @Test
  void transform_shouldThrowException_whenTransformerFails() {
    SampleInput input = new SampleInput("Bob", 20);
    TransformationContext ctx = TransformationContext.builder()
        .nodeContext(NodeContext.builder().fieldName("name").build())
        .build();

    doThrow(new RuntimeException("Transformer failure"))
        .when(mockTransformer)
        .applyTransformations(any(), any(), any());

    assertThrows(DataTransformationException.class, () -> transformer.transform(input, ctx));
  }

  @Test
  void buildTransformedJsonNode_shouldThrowException_whenTransformerFails() {
    SampleInput input = new SampleInput("Eve", 40);
    TransformationContext ctx = TransformationContext.builder()
        .nodeContext(NodeContext.builder().fieldName("name").build())
        .build();

    doThrow(new RuntimeException("Transformer failure"))
        .when(mockTransformer)
        .applyTransformations(any(), any(), any());

    assertThrows(DataTransformationException.class,
        () -> transformer.buildTransformedJsonNode(input, ctx));
  }

  static class SampleInput {

    public String name;
    public int age;

    public SampleInput() {
    }

    public SampleInput(String name, int age) {
      this.name = name;
      this.age = age;
    }
  }
}
