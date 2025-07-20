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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import lombok.Getter;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class responsible for initializing and configuring the {@link Transformer}. It discovers
 * custom operators annotated with {@link RegisterOperator}, and builds all internal components such
 * as strategies and processors.
 */
@Getter
public class TransformerFactory {

  private static final Logger log = LoggerFactory.getLogger(TransformerFactory.class);

  private final Transformer transformer;


  /**
   * Initializes the Transformer using full classpath scan.
   */
  public TransformerFactory() {
    this.transformer = this.initialize();
  }

  /**
   * Initializes the Transformer with restricted package scanning.
   *
   * @param pkgs list of package names to scan for @RegisterOperator annotations
   */
  public TransformerFactory(String... pkgs) {
    this.transformer = this.initialize(pkgs);
  }

  /**
   * Core initializer for setting up the transformer pipeline.
   */
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

  /**
   * Scans the provided packages or classpath for classes annotated with {@link RegisterOperator},
   * validates and instantiates them.
   *
   * @param pkgs packages to scan
   * @return map of operator key to instantiated {@link BaseOperator}
   */
  private Map<String, BaseOperator> discoverOperators(String... pkgs) {
    try {
      ConfigurationBuilder configurationBuilder = new ConfigurationBuilder().setScanners(
          Scanners.TypesAnnotated);
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

  /**
   * Processes discovered operator classes, validates each one, and instantiates them. Performs
   * parallel instantiation using {@code parallelStream}.
   *
   * @param classes set of annotated operator classes
   * @return unmodifiable map of operator key to instantiated operator
   */
  private Map<String, BaseOperator> processDiscoveredClasses(Set<Class<?>> classes) {

    try {
      ConcurrentMap<String, BaseOperator> operatorMap = classes.parallelStream().map(clazz -> {
        // Ensure class implements BaseOperator
        if (!BaseOperator.class.isAssignableFrom(clazz)) {
          throw DataTransformationException.error(ErrorCode.WRONG_ANNOTATION_ERROR,
              Map.of("detailMessage", "Class " + clazz.getName()
                  + " with @RegisterOperator doesn't implement BaseOperator"));
        }

        // Extract annotation key
        String key = clazz.getAnnotation(RegisterOperator.class).value();
        if (key == null || key.trim().isEmpty()) {
          throw DataTransformationException.error(ErrorCode.OPERATOR_KEY_NULL_OR_EMPTY_ERROR,
              Map.of("detailMessage", "Operator key missing on class: " + clazz.getName()));
        }

        try {
          BaseOperator instance = (BaseOperator) clazz.getDeclaredConstructor().newInstance();
          log.info("Registered operator: {} with key: {}", clazz.getName(), key);
          return Map.entry(key, instance);
        } catch (Exception e) {
          throw DataTransformationException.propagate(ErrorCode.INITIALIZATION_OPERATOR_ERROR,
              new Throwable("Failed to instantiate: " + clazz.getName(), e));
        }
      }).collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> {
        throw DataTransformationException.error(ErrorCode.DUPLICATE_OPERATOR_KEY_ERROR,
            Map.of("detailMessage", "Duplicate operator key found: " + a));
      }));

      return Collections.unmodifiableMap(operatorMap);
    } catch (Exception e) {
      log.error("Failed to process discovered operator classes", e);
      throw e;
    }
  }
}
