package io.github.syphen.engine.processor.impl;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.syphen.engine.delegate.NodeTransformer;
import io.github.syphen.engine.processor.JsonProcessor;
import io.github.syphen.model.TransformationContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Processor implementation for transforming JSON arrays. Iterates through each element in the array
 * and applies the transformation if the element is a JSON object.
 *
 * <p>Expected to be used when the root or a field is a JSON array containing POJOs.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonArrayProcessor implements JsonProcessor {

  private NodeTransformer nodeTransformer;


  /**
   * Applies transformations to each object element within the given array node. Ignores elements
   * that are null or not objects.
   *
   * @param node                  the JSON array node containing elements to be transformed
   * @param elementPojoClass      the class of each element in the array
   * @param transformationContext transformation metadata and operators
   * @param <T>                   a subtype of {@link JsonNode}, expected to be an array node
   * @throws IllegalArgumentException if the input node is not an array
   */
  @Override
  public <T extends JsonNode> void transform(T node, Class<?> elementPojoClass,
      TransformationContext transformationContext) {
    for (JsonNode item : node) {
      if (item != null && item.isObject()) {
        nodeTransformer.applyTransformations(item, elementPojoClass, transformationContext);
      }
    }
  }
}
