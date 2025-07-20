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

/**
 * A transformation strategy that applies an operator directly to the field's value.
 * <p>
 * This strategy supports both single values and arrays of values. It retrieves the
 * appropriate {@link BaseOperator} from the {@link OperatorRegistry} based on the
 * {@link TransformationContext}, and applies the operator to the value(s).
 * </p>
 */
@AllArgsConstructor
public class DirectValueTransformationStrategy implements FieldTransformationStrategy {

  /**
   * Registry that maps operator type strings to {@link BaseOperator} implementations.
   */
  private OperatorRegistry operatorRegistry;

  /**
   * Applies a transformation to the specified field in the parent JSON node.
   *
   * @param parentNode      the parent JSON object containing the field
   * @param fieldContext    the transformation context for this specific field
   * @param fieldTypeInfo   metadata about the field’s type, including whether it's a collection
   *
   * @throws IllegalArgumentException if operator is not found or if required parameters are null
   */
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

  /**
   * Applies the operator to each element of an array field.
   *
   * @param fieldValue   the JSON node representing the array
   * @param operator     the transformation operator to apply
   * @param fieldClass   the target class type of each element
   */
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

  /**
   * Resolves the effective operator type from the field context.
   * <p>
   * If a specific operator type is defined in the node context, it is used;
   * otherwise, the default operator type is returned.
   * </p>
   *
   * @param transformationContext the context containing operator preferences
   * @return the operator type string
   */
  private String resolveOperatorType(TransformationContext transformationContext) {
    String specificOperatorType = transformationContext.getNodeContext().getOperateType();
    return !CommonUtil.isNullOrEmpty(specificOperatorType) ? specificOperatorType
        : transformationContext.getDefaultOperatorType();
  }

}
