package io.github.syphen.engine.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.syphen.engine.processor.JsonProcessor;
import io.github.syphen.factory.ProcessorSelector;
import io.github.syphen.model.TransformationContext;
import io.github.syphen.utils.CommonUtil;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NodeTransformer {

  private ProcessorSelector processorSelector;


  public void applyTransformations(JsonNode parentNode, Class<?> pojoClass,
      TransformationContext transformationContext) {
    if (CommonUtil.isAnyNull(parentNode, pojoClass)) {
      return;
    }
    JsonProcessor processor = processorSelector.getProcessor(parentNode);
    if (processor != null) {
      processor.transform(parentNode, pojoClass, transformationContext);
    }
  }
}