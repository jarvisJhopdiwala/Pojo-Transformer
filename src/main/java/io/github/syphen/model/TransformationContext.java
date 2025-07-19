package io.github.syphen.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransformationContext {
  private String defaultOperatorType;
  private NodeContext nodeContext;
}
