package io.github.syphen.engine.processor.impl;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.syphen.engine.delegate.NodeTransformer;
import io.github.syphen.engine.processor.JsonProcessor;
import io.github.syphen.model.TransformationContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonArrayProcessor implements JsonProcessor {
  private NodeTransformer nodeTransformer;

  @Override
  public <T extends JsonNode> void transform(T node, Class<?> elementPojoClass,
      TransformationContext transformationContext) {
    for(JsonNode item : node) {
      if(item != null && item.isObject()) {
        nodeTransformer.applyTransformations(item, elementPojoClass, transformationContext);
      }
    }
  }
}
