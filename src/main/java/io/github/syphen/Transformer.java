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

@SuppressWarnings("unchecked")
@AllArgsConstructor
public class Transformer {

  private final NodeTransformer nodeTransformer;

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