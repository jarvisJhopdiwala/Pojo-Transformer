package io.github.syphen.engine.processor;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.syphen.model.TransformationContext;

public interface JsonProcessor {

  <T extends JsonNode> void transform(T node, Class<?> elementPojoClass,
      TransformationContext transformationContext);
}
