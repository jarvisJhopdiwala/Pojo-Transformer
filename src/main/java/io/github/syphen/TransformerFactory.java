package io.github.syphen;

import io.github.syphen.annotation.RegisterOperator;
import io.github.syphen.cache.PojoSchemaCache;
import io.github.syphen.engine.delegate.NodeTransformer;
import io.github.syphen.engine.processor.FieldProcessor;
import io.github.syphen.engine.processor.impl.JsonArrayProcessor;
import io.github.syphen.engine.processor.impl.JsonObjectProcessor;
import io.github.syphen.engine.strategy.impl.DirectValueTransformationStrategy;
import io.github.syphen.engine.strategy.impl.NestedNodeTransformationStrategy;
import io.github.syphen.exception.DataTransformationException;
import io.github.syphen.exception.ErrorCode;
import io.github.syphen.factory.OperatorRegistry;
import io.github.syphen.factory.ProcessorSelector;
import io.github.syphen.factory.StrategySelector;
import io.github.syphen.operator.BaseOperator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class TransformerFactory {

  private static final Logger log = LoggerFactory.getLogger(TransformerFactory.class);

  private final Transformer transformer;

  public TransformerFactory() {
    this.transformer = this.initialize();
  }

  public TransformerFactory(String... pkgs) {
    this.transformer = this.initialize(pkgs);
  }

  private Transformer initialize(String... pkgs) {
    PojoSchemaCache pojoSchemaCache = new PojoSchemaCache();
    OperatorRegistry operatorRegistry = new OperatorRegistry(discoverOperators(pkgs));
    DirectValueTransformationStrategy directValueTransformationStrategy = new DirectValueTransformationStrategy(
        operatorRegistry);
    NestedNodeTransformationStrategy nestedNodeTransformationStrategy = new NestedNodeTransformationStrategy();
    StrategySelector strategySelector = new StrategySelector(directValueTransformationStrategy,
        nestedNodeTransformationStrategy);

    FieldProcessor fieldProcessor = new FieldProcessor(strategySelector);
    JsonArrayProcessor jsonArrayProcessor = new JsonArrayProcessor();
    JsonObjectProcessor jsonObjectProcessor = new JsonObjectProcessor(fieldProcessor,
        pojoSchemaCache);

    ProcessorSelector processorSelector = new ProcessorSelector(jsonArrayProcessor,
        jsonObjectProcessor);
    NodeTransformer nodeTransformer = new NodeTransformer(processorSelector);
    jsonArrayProcessor.setNodeTransformer(nodeTransformer);
    nestedNodeTransformationStrategy.setNodeTransformer(nodeTransformer);
    return new Transformer(nodeTransformer);
  }

  private Map<String, BaseOperator> discoverOperators(String... pkgs) {
    try {
      ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
      configurationBuilder.setScanners(Scanners.TypesAnnotated);
      if (pkgs == null || pkgs.length == 0) {
        configurationBuilder.addUrls(ClasspathHelper.forJavaClassPath());
        configurationBuilder.addUrls(ClasspathHelper.forClassLoader());
      } else {
        for (String pck : pkgs) {
          configurationBuilder.addUrls(ClasspathHelper.forPackage(pck));
        }
      }
      Reflections reflections = new Reflections(configurationBuilder);
      Set<Class<?>> loadedClasses = reflections.getTypesAnnotatedWith(RegisterOperator.class);
      log.info("Discovered {} operator(s) annotated with @RegisterOperator", loadedClasses.size());
      return processDiscoveredClasses(loadedClasses);
    } catch (Exception e) {
      throw DataTransformationException.propagate(ErrorCode.INITIALIZATION_OPERATOR_ERROR, e);
    }
  }

  private Map<String, BaseOperator> processDiscoveredClasses(Set<Class<?>> classes) {
    Map<String, BaseOperator> operatorMap = new HashMap<>();
    for (Class<?> clazz : classes) {
      // Validate class implements BaseOperator
      if (!BaseOperator.class.isAssignableFrom(clazz)) {
        log.error("Class {} with @RegisterOperator doesn't implement BaseOperator",
            clazz.getName());
        throw DataTransformationException.error(ErrorCode.WRONG_ANNOTATION_ERROR,
            Map.of("detailMessage", "Class " + clazz.getName()
                + " with @RegisterOperator doesn't implement BaseOperator"));
      }

      // Get and validate operator key
      String key = clazz.getAnnotation(RegisterOperator.class).value();
      if (key == null || key.trim().isEmpty()) {
        log.error("Operator type cannot be null or empty for class {}", clazz.getName());
        throw DataTransformationException.error(ErrorCode.OPERATOR_KEY_NULL_OR_EMPTY_ERROR,
            Map.of("detailMessage",
                "Operator type cannot be null or empty for class " + clazz.getName()));
      }

      // Check for duplicate
      if (operatorMap.containsKey(key)) {
        log.error("Duplicate operator key found: {} (class: {})", key, clazz.getName());
        throw DataTransformationException.error(ErrorCode.DUPLICATE_OPERATOR_KEY_ERROR,
            Map.of("detailMessage", "Duplicate operator key found: " + key));
      }

      // Instantiate operator
      try {
        operatorMap.put(key, (BaseOperator) clazz.getDeclaredConstructor().newInstance());
        log.info("Registered operator: {} with key: {}", clazz.getName(), key);
      } catch (Exception e) {
        log.error("Failed to instantiate operator: {}", clazz.getName(), e);
        throw DataTransformationException.propagate(ErrorCode.INITIALIZATION_OPERATOR_ERROR,
            new Throwable("Failed to instantiate: " + clazz.getName(), e));
      }
    }
    return Collections.unmodifiableMap(operatorMap);
  }
}
