package io.github.syphen.engine.processor;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.syphen.engine.strategy.FieldTransformationStrategy;
import io.github.syphen.factory.StrategySelector;
import io.github.syphen.model.FieldTypeDescriptor;
import io.github.syphen.model.TransformationContext;
import io.github.syphen.utils.CommonUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Processes a single field of a JSON object using a strategy pattern.
 * <p>
 * It determines the appropriate {@link FieldTransformationStrategy} based on
 * the {@link TransformationContext}, and delegates the transformation work
 * to that strategy.
 * </p>
 */
@NoArgsConstructor
@AllArgsConstructor
public class FieldProcessor {

  /** StrategySelector is used to dynamically determine transformation strategy for a field. */
  private StrategySelector strategySelector;


  /**
   * Applies the appropriate field-level transformation strategy to a field node.
   *
   * @param parentNode         the parent JSON object node containing the field
   * @param fieldContext       the transformation context for this field
   * @param fieldTypeDescriptor metadata describing the field's Java type and transformation rules
   *
   * @throws IllegalArgumentException if strategySelector is null or any required parameter is null
   * @throws IllegalStateException if no valid strategy is found for the given context
   */
  public void processField(ObjectNode parentNode, TransformationContext fieldContext,
      FieldTypeDescriptor fieldTypeDescriptor) {
    if (CommonUtil.nonValidNodeContext(fieldContext)) {
      return;
    }
    FieldTransformationStrategy strategy = strategySelector.determineStrategy(fieldContext);
    strategy.process(parentNode, fieldContext, fieldTypeDescriptor);
  }
}
