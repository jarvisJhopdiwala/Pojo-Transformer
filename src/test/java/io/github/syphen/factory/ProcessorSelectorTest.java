package io.github.syphen.factory;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import io.github.syphen.engine.processor.JsonProcessor;
import io.github.syphen.engine.processor.impl.JsonArrayProcessor;
import io.github.syphen.engine.processor.impl.JsonObjectProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProcessorSelectorTest {

  private JsonArrayProcessor mockArrayProcessor;
  private JsonObjectProcessor mockObjectProcessor;
  private ProcessorSelector processorSelector;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setup() {
    mockArrayProcessor = mock(JsonArrayProcessor.class);
    mockObjectProcessor = mock(JsonObjectProcessor.class);
    processorSelector = new ProcessorSelector(mockArrayProcessor, mockObjectProcessor);
  }

  @Test
  void testGetProcessor_returnsObjectProcessor_whenNodeIsObject() {
    ObjectNode objectNode = objectMapper.createObjectNode();
    JsonProcessor processor = processorSelector.getProcessor(objectNode);
    assertSame(mockObjectProcessor, processor);
  }

  @Test
  void testGetProcessor_returnsArrayProcessor_whenNodeIsArray() {
    ArrayNode arrayNode = objectMapper.createArrayNode();
    JsonProcessor processor = processorSelector.getProcessor(arrayNode);
    assertSame(mockArrayProcessor, processor);
  }

  @Test
  void testGetProcessor_returnsNull_whenNodeIsText() {
    TextNode textNode = TextNode.valueOf("test");
    JsonProcessor processor = processorSelector.getProcessor(textNode);
    assertNull(processor);
  }

  @Test
  void testGetProcessor_returnsNull_whenNodeIsNumber() {
    NumericNode numberNode = IntNode.valueOf(42);
    JsonProcessor processor = processorSelector.getProcessor(numberNode);
    assertNull(processor);
  }

  @Test
  void testGetProcessor_returnsNull_whenNodeIsNull() {
    NullNode nullNode = NullNode.getInstance();
    JsonProcessor processor = processorSelector.getProcessor(nullNode);
    assertNull(processor);
  }

  @Test
  void testGetProcessor_returnsNull_whenNodeIsBoolean() {
    BooleanNode booleanNode = BooleanNode.valueOf(true);
    JsonProcessor processor = processorSelector.getProcessor(booleanNode);
    assertNull(processor);
  }
}