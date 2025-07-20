package io.github.syphen.engine.strategy.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.syphen.engine.delegate.NodeTransformer;
import io.github.syphen.engine.strategy.FieldTransformationStrategy;
import io.github.syphen.model.FieldTypeDescriptor;
import io.github.syphen.model.TransformationContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Strategy for transforming nested POJO nodes within a JSON object.
 * <p>
 * Delegates the transformation logic to a {@link NodeTransformer}, which handles
 * the recursive transformation of nested fields or objects.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NestedNodeTransformationStrategy implements FieldTransformationStrategy {

  /** Transformer used to recursively apply transformations to nested POJO nodes. */
  private NodeTransformer nodeTransformer;

  /**
   * Applies transformation to a nested node inside the parent JSON object.
   *
   * @param parentNode      The root JSON object containing the field to transform.
   * @param fieldContext    The transformation context associated with the specific field.
   * @param fieldTypeInfo   Metadata describing the type of the field (including the nested type).
   *
   * @throws IllegalArgumentException if required arguments are null or invalid.
   */
  @Override
  public void process(ObjectNode parentNode, TransformationContext fieldContext,
      FieldTypeDescriptor fieldTypeInfo) {
    JsonNode nodeValue = parentNode.get(fieldContext.getNodeContext().getFieldName());
    nodeTransformer.applyTransformations(nodeValue, fieldTypeInfo.getEffectiveType(), fieldContext);
  }
}
