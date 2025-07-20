package io.github.syphen.engine.delegate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.syphen.engine.processor.JsonProcessor;
import io.github.syphen.factory.ProcessorSelector;
import io.github.syphen.model.TransformationContext;
import io.github.syphen.utils.CommonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class NodeTransformerTest {

  private ProcessorSelector processorSelector;
  private NodeTransformer nodeTransformer;

  @BeforeEach
  void setUp() {
    processorSelector = mock(ProcessorSelector.class);
    nodeTransformer = new NodeTransformer(processorSelector);
  }

  @Test
  void testApplyTransformations_validInputs_callsProcessor() {
    JsonNode mockNode = mock(JsonNode.class);
    Class<?> pojoClass = Dummy.class;
    TransformationContext context = mock(TransformationContext.class);
    JsonProcessor processor = mock(JsonProcessor.class);

    try (MockedStatic<CommonUtil> commonUtilMock = mockStatic(CommonUtil.class)) {
      commonUtilMock.when(() -> CommonUtil.isAnyNull(mockNode, pojoClass))
          .thenReturn(false);

      when(processorSelector.getProcessor(mockNode)).thenReturn(processor);

      nodeTransformer.applyTransformations(mockNode, pojoClass, context);

      verify(processor).transform(mockNode, pojoClass, context);
    }
  }

  @Test
  void testApplyTransformations_nullInputs_doesNothing() {
    JsonNode mockNode = mock(JsonNode.class);
    Class<?> pojoClass = Dummy.class;
    TransformationContext context = mock(TransformationContext.class);

    try (MockedStatic<CommonUtil> commonUtilMock = mockStatic(CommonUtil.class)) {
      commonUtilMock.when(() -> CommonUtil.isAnyNull(mockNode, pojoClass))
          .thenReturn(true);

      nodeTransformer.applyTransformations(mockNode, pojoClass, context);

      verifyNoInteractions(processorSelector);
    }
  }

  @Test
  void testApplyTransformations_noProcessor_doesNothing() {
    JsonNode mockNode = mock(JsonNode.class);
    Class<?> pojoClass = Dummy.class;
    TransformationContext context = mock(TransformationContext.class);

    try (MockedStatic<CommonUtil> commonUtilMock = mockStatic(CommonUtil.class)) {
      commonUtilMock.when(() -> CommonUtil.isAnyNull(mockNode, pojoClass))
          .thenReturn(false);

      when(processorSelector.getProcessor(mockNode)).thenReturn(null);

      nodeTransformer.applyTransformations(mockNode, pojoClass, context);

      // No transform should be called, but processorSelector is interacted with
      verify(processorSelector).getProcessor(mockNode);
    }
  }

  //   Dummy class for testing purposes
  private static class Dummy {

  }
}
