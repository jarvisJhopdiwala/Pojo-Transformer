package io.github.syphen.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeContext {
  private String fieldName;
  private String operateType;
  private boolean transformAllFields;
  private NodeContext[] skipFields;
  private NodeContext[] transformFields;
}