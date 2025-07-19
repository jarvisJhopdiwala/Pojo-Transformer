package io.github.syphen.factory;

import io.github.syphen.engine.strategy.FieldTransformationStrategy;
import io.github.syphen.engine.strategy.impl.DirectValueTransformationStrategy;
import io.github.syphen.engine.strategy.impl.NestedNodeTransformationStrategy;
import io.github.syphen.model.NodeContext;
import io.github.syphen.model.TransformationContext;
import io.github.syphen.utils.CommonUtil;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StrategySelector {

  private DirectValueTransformationStrategy directValueTransformationStrategy;
  private NestedNodeTransformationStrategy nestedNodeTransformationStrategy;

  public FieldTransformationStrategy determineStrategy(
      TransformationContext transformationContext) {
    NodeContext fieldContext = transformationContext.getNodeContext();

    // base case for operating on the field
    if (CommonUtil.allNull(fieldContext.getSkipFields(), fieldContext.getTransformFields())
        && !fieldContext.isTransformAllFields()) {
      return directValueTransformationStrategy;
    }
    // in rest of the cases will query inside the subClasses
    return nestedNodeTransformationStrategy;
  }
}
