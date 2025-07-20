package io.github.syphen.engine.processor.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.syphen.cache.PojoSchemaCache;
import io.github.syphen.engine.processor.FieldProcessor;
import io.github.syphen.exception.DataTransformationException;
import io.github.syphen.exception.ErrorCode;
import io.github.syphen.model.FieldTypeDescriptor;
import io.github.syphen.model.NodeContext;
import io.github.syphen.model.TransformationContext;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JsonObjectProcessorTest {

  private static final Class<DummyPojo> POJO_CLASS = DummyPojo.class;
  private final ObjectMapper objectMapper = new ObjectMapper();
  @Mock
  private FieldProcessor fieldProcessor;
  @Mock
  private PojoSchemaCache pojoSchemaCache;
  @InjectMocks
  private JsonObjectProcessor jsonObjectProcessor;

  @BeforeEach
  void setup() {
    // Ensure CommonUtil returns false for nonValidNodeContext
    // You can remove this if CommonUtil is not mocked
  }

  @Test
  void testTransform_appliesTransformation_whenFieldsPresentInSchema() {
    // Given
    ObjectNode node = objectMapper.createObjectNode();
    node.put("name", "John");

    NodeContext nodeContext = NodeContext.builder()
        .fieldName("root")
        .transformFields(new NodeContext[]{NodeContext.builder().fieldName("name").build()})
        .build();

    TransformationContext context = TransformationContext.builder()
        .defaultOperatorType("noop")
        .nodeContext(nodeContext)
        .build();

    FieldTypeDescriptor descriptor = FieldTypeDescriptor.builder().build(); // stub descriptor
    when(pojoSchemaCache.get(POJO_CLASS)).thenReturn(Map.of("name", descriptor));

    // When
    jsonObjectProcessor.transform(node, POJO_CLASS, context);

    // Then
    verify(fieldProcessor).processField(eq(node), any(TransformationContext.class), eq(descriptor));
  }

  @Test
  void testTransform_doesNothing_whenFieldContextMapEmpty() {
    // Given
    ObjectNode node = objectMapper.createObjectNode();

    TransformationContext context = TransformationContext.builder()
        .defaultOperatorType("noop")
        .nodeContext(null) // triggers CommonUtil.nonValidNodeContext() to return true
        .build();

    // When
    jsonObjectProcessor.transform(node, POJO_CLASS, context);

    // Then
    verifyNoInteractions(fieldProcessor);
  }

  @Test
  void testTransform_throwsException_whenFieldMissingInSchema() {
    // Given
    ObjectNode node = objectMapper.createObjectNode();
    node.put("name", "Jane");

    NodeContext nodeContext = NodeContext.builder()
        .fieldName("root")
        .transformFields(new NodeContext[]{NodeContext.builder().fieldName("name").build()})
        .build();

    TransformationContext context = TransformationContext.builder()
        .defaultOperatorType("noop")
        .nodeContext(nodeContext)
        .build();

    when(pojoSchemaCache.get(POJO_CLASS)).thenReturn(Map.of()); // schema doesn't have 'name'

    // When + Then
    DataTransformationException ex = assertThrows(DataTransformationException.class,
        () -> jsonObjectProcessor.transform(node, POJO_CLASS, context));

    // Verify error code and message
    assert ex.getCode().equals(ErrorCode.FIELD_NOT_EXIST.name());
    assert ex.getContext().get("detailMessage")
        .equals(
            "Field 'name' does not exist in class io.github.syphen.engine.processor.impl.JsonObjectProcessorTest$DummyPojo");
  }

  public static class DummyPojo {

    private String name;
    private int age;
  }
}