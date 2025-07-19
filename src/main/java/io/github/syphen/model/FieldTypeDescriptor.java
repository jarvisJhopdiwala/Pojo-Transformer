package io.github.syphen.model;

import io.github.syphen.utils.CommonUtil;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FieldTypeDescriptor {
  Class<?> clazz;
  Class<?>genericType;

  public boolean isCollection() {
    return CommonUtil.isCollectionType(clazz);
  }

  public Class<?> getEffectiveType() {
    return genericType != null ? genericType : clazz;
  }
}
