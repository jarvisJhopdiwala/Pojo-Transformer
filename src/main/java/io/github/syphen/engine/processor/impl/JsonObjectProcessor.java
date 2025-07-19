package io.github.syphen.engine.processor.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.syphen.cache.PojoSchemaCache;
import io.github.syphen.engine.processor.FieldProcessor;
import io.github.syphen.engine.processor.JsonProcessor;
import io.github.syphen.exception.DataTransformationException;
import io.github.syphen.exception.ErrorCode;
import io.github.syphen.model.FieldTypeDescriptor;
import io.github.syphen.model.NodeContext;
import io.github.syphen.model.TransformationContext;
import io.github.syphen.utils.CommonUtil;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JsonObjectProcessor implements JsonProcessor {

  private FieldProcessor fieldProcessor;
  private PojoSchemaCache pojoSchemaCache;

  public <T extends JsonNode> void transform(T node, Class<?> pojoClass,
      TransformationContext transformationContext) {
    final Map<String, TransformationContext> fieldSpecificContexts = buildFieldOperationMap(
        transformationContext, pojoClass);
    if (fieldSpecificContexts.isEmpty()) {
      return;
    }
    final Map<String, FieldTypeDescriptor> fieldTypes = pojoSchemaCache.get(pojoClass);
    for (Map.Entry<String, TransformationContext> entry : fieldSpecificContexts.entrySet()) {
      FieldTypeDescriptor fieldTypeDescriptor = fieldTypes.get(entry.getKey());
      if (fieldTypeDescriptor == null) {
        throw DataTransformationException.error(ErrorCode.FIELD_NOT_EXIST, Map.of("detailMessage",
            "Field '" + entry.getKey() + "' does not exist in class " + pojoClass.getName()));
      }
      fieldProcessor.processField((ObjectNode) node, entry.getValue(), fieldTypeDescriptor);
    }
  }

  private Map<String, TransformationContext> buildFieldOperationMap(
      TransformationContext transformationContext, Class<?> pojoClass) {
    if (CommonUtil.nonValidNodeContext(transformationContext)) {
      return Collections.emptyMap();
    }
    NodeContext nodeContext = transformationContext.getNodeContext();
    Map<String, TransformationContext> givenFieldContextMap = new HashMap<>();

    // in nodeContext of this field putting fieldName, operatorType and the defaultOperatorType.
    if (nodeContext.isTransformAllFields()) {
      for (Field field : pojoClass.getDeclaredFields()) {
        givenFieldContextMap.put(field.getName(),
            CommonUtil.createTransformationContext(field.getName(), nodeContext.getOperateType(),
                transformationContext.getDefaultOperatorType()));
      }
    }
    // removing skip fields that are present into the map
    if (nodeContext.getSkipFields() != null) {
      for (NodeContext skipFieldContext : nodeContext.getSkipFields()) {
        givenFieldContextMap.remove(skipFieldContext.getFieldName());
      }
    }

    // inserting specific fields that explicitly mentioned in the nodeContext
    if (nodeContext.getTransformFields() != null) {
      for (NodeContext transformFieldContext : nodeContext.getTransformFields()) {
        givenFieldContextMap.put(transformFieldContext.getFieldName(),
            CommonUtil.createTransformationContext(transformationContext.getDefaultOperatorType(),
                transformFieldContext));
      }
    }
    return givenFieldContextMap;
  }
}