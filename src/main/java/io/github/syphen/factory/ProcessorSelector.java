package io.github.syphen.factory;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.syphen.engine.processor.JsonProcessor;
import io.github.syphen.engine.processor.impl.JsonArrayProcessor;
import io.github.syphen.engine.processor.impl.JsonObjectProcessor;
import lombok.AllArgsConstructor;

/**
 * Selector class responsible for returning the appropriate {@link JsonProcessor}
 * implementation based on the type of a given {@link JsonNode}.
 * <p>
 * - Returns {@link JsonObjectProcessor} if node is an object. <br>
 * - Returns {@link JsonArrayProcessor} if node is an array. <br>
 * - Returns {@code null} for all other node types (e.g., primitive, null).
 * </p>
 *
 * <p><b>Note:</b> It is the caller's responsibility to handle the case when {@code null} is returned.</p>
 */
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
