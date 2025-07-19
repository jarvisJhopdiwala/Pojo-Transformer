package io.github.syphen.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.syphen.model.FieldTypeDescriptor;
import io.github.syphen.utils.CommonUtil;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PojoSchemaCache {

  private static final int MAX_CACHE_SIZE = 1000;
  private static final int EXPIRE_AFTER_WRITE_HOURS = 1;

  private final Cache<Class<?>, Map<String, FieldTypeDescriptor>> fieldAttributeCache;

  public PojoSchemaCache() {
    this.fieldAttributeCache = Caffeine.newBuilder().maximumSize(MAX_CACHE_SIZE)
        .expireAfterWrite(EXPIRE_AFTER_WRITE_HOURS, TimeUnit.HOURS)
        .build();
  }

  public Map<String, FieldTypeDescriptor> get(Class<?> key) {
    Objects.requireNonNull(key, "Class cannot be null");
    return fieldAttributeCache.get(key, CommonUtil::inspectClass);
  }
}
