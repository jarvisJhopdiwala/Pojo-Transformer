package io.github.syphen.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;


@UtilityClass
public class MapperUtil {
  private static ObjectMapper mapper;

  static {
    configureObjectMapper();
  }
  public static void configureObjectMapper() {
    mapper = JsonMapper.builder()
        .configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
        .build();
    mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    mapper.setVisibility(PropertyAccessor.GETTER, Visibility.NONE);
    mapper.setVisibility(PropertyAccessor.IS_GETTER, Visibility.NONE);
    mapper.setVisibility(PropertyAccessor.SETTER, Visibility.NONE);
    mapper.setVisibility(PropertyAccessor.CREATOR, Visibility.NONE);

    mapper.registerModule(new JavaTimeModule());
  }

  public static ObjectMapper getObjectMapper() {
    return mapper;
  }
}
