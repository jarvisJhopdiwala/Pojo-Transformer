package io.github.syphen.engine.strategy;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.syphen.model.FieldTypeDescriptor;
import io.github.syphen.model.TransformationContext;

public interface FieldTransformationStrategy {
  void process(ObjectNode parentNode, TransformationContext fieldContext, FieldTypeDescriptor fieldTypeInfo);
}
