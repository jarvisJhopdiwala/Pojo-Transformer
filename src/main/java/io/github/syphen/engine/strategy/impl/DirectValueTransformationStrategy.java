package io.github.syphen.engine.strategy.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.syphen.engine.strategy.FieldTransformationStrategy;
import io.github.syphen.factory.OperatorRegistry;
import io.github.syphen.model.FieldTypeDescriptor;
import io.github.syphen.model.TransformationContext;
import io.github.syphen.operator.BaseOperator;
import io.github.syphen.utils.CommonUtil;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DirectValueTransformationStrategy implements FieldTransformationStrategy {

  private OperatorRegistry operatorRegistry;

  @Override
  public void process(ObjectNode parentNode, TransformationContext fieldContext,
      FieldTypeDescriptor fieldTypeInfo) {
    JsonNode fieldValue = parentNode.get(fieldContext.getNodeContext().getFieldName());

    // 1. Determine which operator to use based on the context.
    final String operatorType = resolveOperatorType(fieldContext);
    final BaseOperator operator = operatorRegistry.getOperator(operatorType);

    // 2. Apply the operator based on whether the field is a collection or a single value.
    if (fieldTypeInfo.isCollection()) {
      transformArrayElements(fieldValue, operator, fieldTypeInfo.getEffectiveType());
      return;
    }

    parentNode.replace(fieldContext.getNodeContext().getFieldName(),
        operator.apply(fieldValue, fieldTypeInfo.getEffectiveType()));
  }

  private void transformArrayElements(JsonNode fieldValue, BaseOperator operator,
      Class<?> fieldClass) {
    if (fieldValue == null || !fieldValue.isArray()) {
      return;
    }
    ArrayNode arrayNode = (ArrayNode) fieldValue;
    for (int i = 0; i < arrayNode.size(); i++) {
      JsonNode element = arrayNode.get(i);
      arrayNode.set(i, operator.apply(element, fieldClass));
    }
  }

  private String resolveOperatorType(TransformationContext transformationContext) {
    String specificOperatorType = transformationContext.getNodeContext().getOperateType();
    return !CommonUtil.isNullOrEmpty(specificOperatorType) ? specificOperatorType
        : transformationContext.getDefaultOperatorType();
  }

}
