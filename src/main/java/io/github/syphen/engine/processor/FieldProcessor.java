package io.github.syphen.engine.processor;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.syphen.engine.strategy.FieldTransformationStrategy;
import io.github.syphen.factory.StrategySelector;
import io.github.syphen.model.FieldTypeDescriptor;
import io.github.syphen.model.TransformationContext;
import io.github.syphen.utils.CommonUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class FieldProcessor {

  private StrategySelector strategySelector;

  public void processField(ObjectNode parentNode, TransformationContext fieldContext,
      FieldTypeDescriptor fieldTypeDescriptor) {
    if (CommonUtil.nonValidNodeContext(fieldContext)) {
      return;
    }
    FieldTransformationStrategy strategy = strategySelector.determineStrategy(fieldContext);
    strategy.process(parentNode, fieldContext, fieldTypeDescriptor);
  }
}
