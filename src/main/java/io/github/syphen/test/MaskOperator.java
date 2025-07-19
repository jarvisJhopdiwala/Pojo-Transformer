package io.github.syphen.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.github.syphen.annotation.RegisterOperator;
import io.github.syphen.operator.BaseOperator;
import io.github.syphen.utils.CommonUtil;

@RegisterOperator("mask")
public class MaskOperator extends BaseOperator {

  private static final JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;

  @Override
  public JsonNode apply(JsonNode input, Class<?> inputClass) {
    if (inputClass == String.class) {
      if (input == null || input.isNull() || CommonUtil.isNullOrEmpty(input.asText())) {
        return jsonNodeFactory.textNode("###");
      }
      return jsonNodeFactory.textNode("***");
    }
    if (inputClass == Integer.class || inputClass == int.class) {
      if (input == null || input.isNull()) {
        return jsonNodeFactory.numberNode(0);
      }
      return jsonNodeFactory.numberNode(1);
    }
    return input;
  }
}
