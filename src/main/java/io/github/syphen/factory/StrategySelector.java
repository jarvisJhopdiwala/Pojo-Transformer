package io.github.syphen.factory;

import io.github.syphen.engine.strategy.FieldTransformationStrategy;
import io.github.syphen.engine.strategy.impl.DirectValueTransformationStrategy;
import io.github.syphen.engine.strategy.impl.NestedNodeTransformationStrategy;
import io.github.syphen.model.NodeContext;
import io.github.syphen.model.TransformationContext;
import io.github.syphen.utils.CommonUtil;
import lombok.AllArgsConstructor;

/**
 * StrategySelector is responsible for choosing an appropriate transformation strategy
 * based on the structure of the transformation context.
 * <p>
 * It selects either:
 * <ul>
 *   <li>{@link DirectValueTransformationStrategy} — when transformation is directly applied to a field.</li>
 *   <li>{@link NestedNodeTransformationStrategy} — when transformation must be applied to a nested structure.</li>
 * </ul>
 */
@AllArgsConstructor
public class StrategySelector {

  /** Strategy to handle direct field transformation. */
  private DirectValueTransformationStrategy directValueTransformationStrategy;
  /** Strategy to handle nested field transformation (e.g., nested POJOs). */
  private NestedNodeTransformationStrategy nestedNodeTransformationStrategy;


  /**
   * Determines which strategy should be applied based on the {@link TransformationContext}.
   *
   * @param transformationContext The current transformation context.
   * @return The appropriate {@link FieldTransformationStrategy} implementation.
   * @throws IllegalArgumentException if the context or node context is null.
   */
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
