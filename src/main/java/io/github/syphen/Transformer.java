package io.github.syphen;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.syphen.engine.delegate.NodeTransformer;
import io.github.syphen.exception.DataTransformationException;
import io.github.syphen.exception.ErrorCode;
import io.github.syphen.model.TransformationContext;
import io.github.syphen.utils.CommonUtil;
import io.github.syphen.utils.MapperUtil;
import lombok.AllArgsConstructor;


/**
 * Main transformation entry point for transforming any object using a given {@link TransformationContext}.
 * Applies custom operator logic and field-level transformations defined in the framework.
 */
@SuppressWarnings("unchecked")
@AllArgsConstructor
public class Transformer {

  private final NodeTransformer nodeTransformer;

  /**
   * Transforms the given input object using the provided transformation context.
   * Converts the object to a transformed JsonNode and maps it back to the original object type.
   *
   * @param input the input object to be transformed
   * @param transformationContext context containing transformation instructions
   * @param <T> type of the input and output object
   * @return transformed object of the same type
   * @throws DataTransformationException if transformation fails
   */
  public <T> T transform(T input, TransformationContext transformationContext) {
    if (input == null || CommonUtil.nonValidNodeContext(transformationContext)) {
      return input;
    }
    try {
      JsonNode node = buildTransformedJsonNode(input, transformationContext);
      return (T) MapperUtil.getObjectMapper().convertValue(node, input.getClass());
    } catch (Exception e) {
      throw DataTransformationException.propagate(ErrorCode.TRANSFORMATION_ERROR, e);
    }
  }

  /**
   * Builds the intermediate transformed JsonNode representation of the input.
   * This is useful when only a JSON structure is required without reconstructing the object.
   *
   * @param input the input object to be transformed
   * @param transformationContext context containing transformation rules
   * @param <T> type of the input object
   * @return transformed JsonNode or null if input is invalid
   * @throws DataTransformationException if transformation fails
   */
  public <T> JsonNode buildTransformedJsonNode(T input,
      TransformationContext transformationContext) {
    if (input == null || CommonUtil.nonValidNodeContext(transformationContext)) {
      return null;
    }
    try {
      ObjectNode node = MapperUtil.getObjectMapper().valueToTree(input);
      nodeTransformer.applyTransformations(node, input.getClass(), transformationContext);
      return node;
    } catch (Exception e) {
      throw DataTransformationException.propagate(ErrorCode.TRANSFORMATION_ERROR, e);
    }
  }
}