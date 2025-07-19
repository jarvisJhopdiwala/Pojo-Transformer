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


@Data
@NoArgsConstructor
@AllArgsConstructor
public class NestedNodeTransformationStrategy implements FieldTransformationStrategy {

  private NodeTransformer nodeTransformer;

  @Override
  public void process(ObjectNode parentNode, TransformationContext fieldContext,
      FieldTypeDescriptor fieldTypeInfo) {
    JsonNode nodeValue = parentNode.get(fieldContext.getNodeContext().getFieldName());
    nodeTransformer.applyTransformations(nodeValue, fieldTypeInfo.getEffectiveType(), fieldContext);
  }
}
