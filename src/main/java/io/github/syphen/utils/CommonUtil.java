package io.github.syphen.utils;


import io.github.syphen.model.FieldTypeDescriptor;
import io.github.syphen.model.NodeContext;
import io.github.syphen.model.TransformationContext;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UtilityClass
public class CommonUtil {

  private static final Logger log = LoggerFactory.getLogger(CommonUtil.class);

  public static Map<String, FieldTypeDescriptor> inspectClass(Class<?> clazz) {
    if (clazz == null || clazz == Object.class || clazz.isInterface() || clazz.isPrimitive()) {
      return new HashMap<>();
    }
    Map<String, FieldTypeDescriptor> fields = inspectClass(clazz.getSuperclass());
    Arrays.stream(clazz.getDeclaredFields())
        .filter(field -> !field.isSynthetic())
        .forEach(field -> fields.put(field.getName(), getFieldTypeInfo(field)));
    return fields;
  }

  public static FieldTypeDescriptor getFieldTypeInfo(Field field) {
    if (field == null) {
      return null;
    }
    return FieldTypeDescriptor.builder()
        .clazz(field.getType())
        .genericType(isCollectionType(field.getType()) ? getGenericClass(field) : null)
        .build();
  }

  public static Class<?> getGenericClass(Field field) {
    try {
      if (field.getGenericType() instanceof ParameterizedType parameterizedType) {
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        if (typeArguments.length > 0 && typeArguments[0] instanceof Class<?> genericClass) {
          return genericClass;
        }
      }
    } catch (Exception e) {
      log.error("Error getting generic class for field='{}': {}", field.getName(), e.getMessage(),
          e);
    }
    return null;
  }

  public boolean isCollectionType(Class<?> clazz) {
    return clazz != null && Collection.class.isAssignableFrom(clazz);
  }

  public static NodeContext createFieldContext(final String fieldName, final String operateType) {
    return NodeContext.builder().fieldName(fieldName).operateType(operateType).build();
  }

  public static TransformationContext createTransformationContext(final String fieldName,
      final String operateType, final String defaultOperatorType) {
    return TransformationContext.builder().defaultOperatorType(defaultOperatorType)
        .nodeContext(createFieldContext(fieldName, operateType)).build();
  }

  public static TransformationContext createTransformationContext(final String defaultOperatorType,
      final NodeContext nodeContext) {
    return TransformationContext.builder().defaultOperatorType(defaultOperatorType)
        .nodeContext(nodeContext).build();
  }

  public static boolean allNull(Object... objects) {
    return Arrays.stream(objects).allMatch(Objects::isNull);
  }

  public static boolean isAnyNull(Object... objects) {
    return Arrays.stream(objects).anyMatch(Objects::isNull);
  }

  public static boolean nonValidNodeContext(TransformationContext transformationContext) {
    return Optional.ofNullable(transformationContext).map(TransformationContext::getNodeContext)
        .map(NodeContext::getFieldName).isEmpty();
  }

  public static boolean isNullOrEmpty(String string) {
    return string == null || string.isEmpty();
  }
}