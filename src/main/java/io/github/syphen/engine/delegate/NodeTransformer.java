package io.github.syphen.engine.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.syphen.engine.processor.JsonProcessor;
import io.github.syphen.factory.ProcessorSelector;
import io.github.syphen.model.TransformationContext;
import io.github.syphen.utils.CommonUtil;
import lombok.AllArgsConstructor;

/**
 * Delegates transformation of a JSON node by selecting and applying the appropriate processor.
 * This class abstracts the application of transformation strategies on nested JSON structures.
 */
@AllArgsConstructor
public class NodeTransformer {

  private ProcessorSelector processorSelector;

  /**
   * Applies transformations on the given parent JSON node using the processor
   * selected based on the node's type.
   *
   * @param parentNode the parent JSON node to be transformed
   * @param pojoClass the Java POJO class type corresponding to the JSON structure
   * @param transformationContext transformation metadata and rules to apply
   */
  public void applyTransformations(JsonNode parentNode, Class<?> pojoClass,
      TransformationContext transformationContext) {
    if (CommonUtil.isAnyNull(parentNode, pojoClass)) {
      return;
    }
    // Select the appropriate processor for this node
    JsonProcessor processor = processorSelector.getProcessor(parentNode);
    if (processor != null) {
      processor.transform(parentNode, pojoClass, transformationContext);
    }
  }
}