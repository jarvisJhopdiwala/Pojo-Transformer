package io.github.syphen.operator;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class BaseOperator {

  public abstract JsonNode apply(JsonNode input, Class<?> inputClass);
}
