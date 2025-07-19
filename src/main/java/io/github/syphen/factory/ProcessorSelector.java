package io.github.syphen.factory;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.syphen.engine.processor.JsonProcessor;
import io.github.syphen.engine.processor.impl.JsonArrayProcessor;
import io.github.syphen.engine.processor.impl.JsonObjectProcessor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProcessorSelector {

  private final JsonArrayProcessor jsonArrayProcessor;
  private final JsonObjectProcessor jsonObjectProcessor;

  public JsonProcessor getProcessor(JsonNode node) {
    if (node.isObject()) {
      return jsonObjectProcessor;
    } else if (node.isArray()) {
      return jsonArrayProcessor;
    }
    return null;
  }
}
