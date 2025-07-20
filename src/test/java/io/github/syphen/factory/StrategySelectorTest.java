package io.github.syphen.factory;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.syphen.engine.strategy.FieldTransformationStrategy;
import io.github.syphen.engine.strategy.impl.DirectValueTransformationStrategy;
import io.github.syphen.engine.strategy.impl.NestedNodeTransformationStrategy;
import io.github.syphen.model.NodeContext;
import io.github.syphen.model.TransformationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StrategySelectorTest {

  private DirectValueTransformationStrategy directStrategy;
  private NestedNodeTransformationStrategy nestedStrategy;
  private StrategySelector strategySelector;

  @BeforeEach
  void setUp() {
    directStrategy = new DirectValueTransformationStrategy(
        null); // no registry needed for strategy selector test
    nestedStrategy = new NestedNodeTransformationStrategy(null);
    strategySelector = new StrategySelector(directStrategy, nestedStrategy);
  }

  @Test
  void testDetermineStrategy_returnsDirectStrategy_whenNoNestedFieldsAndTransformAllFalse() {
    NodeContext nodeContext = NodeContext.builder()
        .skipFields(null)
        .transformFields(null)
        .transformAllFields(false)
        .build();

    TransformationContext context = TransformationContext.builder()
        .nodeContext(nodeContext)
        .build();

    FieldTransformationStrategy strategy = strategySelector.determineStrategy(context);
    assertSame(directStrategy, strategy);
  }

  @Test
  void testDetermineStrategy_returnsNestedStrategy_whenSkipFieldsIsNonEmpty() {
    NodeContext nodeContext = NodeContext.builder()
        .skipFields(new NodeContext[]{NodeContext.builder().fieldName("a").build()})
        .transformFields(null)
        .transformAllFields(false)
        .build();

    TransformationContext context = TransformationContext.builder()
        .nodeContext(nodeContext)
        .build();

    FieldTransformationStrategy strategy = strategySelector.determineStrategy(context);
    assertSame(nestedStrategy, strategy);
  }

  @Test
  void testDetermineStrategy_returnsNestedStrategy_whenTransformFieldsIsNonEmpty() {
    NodeContext nodeContext = NodeContext.builder()
        .skipFields(null)
        .transformFields(new NodeContext[]{NodeContext.builder().fieldName("b").build()})
        .transformAllFields(false)
        .build();

    TransformationContext context = TransformationContext.builder()
        .nodeContext(nodeContext)
        .build();

    FieldTransformationStrategy strategy = strategySelector.determineStrategy(context);
    assertSame(nestedStrategy, strategy);
  }

  @Test
  void testDetermineStrategy_returnsNestedStrategy_whenTransformAllFieldsTrue() {
    NodeContext nodeContext = NodeContext.builder()
        .skipFields(null)
        .transformFields(null)
        .transformAllFields(true)
        .build();

    TransformationContext context = TransformationContext.builder()
        .nodeContext(nodeContext)
        .build();

    FieldTransformationStrategy strategy = strategySelector.determineStrategy(context);
    assertSame(nestedStrategy, strategy);
  }

  @Test
  void testDetermineStrategy_throws_whenNodeContextIsNull() {
    TransformationContext context = TransformationContext.builder()
        .nodeContext(null)
        .build();

    assertThrows(NullPointerException.class, () -> strategySelector.determineStrategy(context));
  }

  @Test
  void testDetermineStrategy_throws_whenTransformationContextIsNull() {
    assertThrows(NullPointerException.class, () -> strategySelector.determineStrategy(null));
  }
}
