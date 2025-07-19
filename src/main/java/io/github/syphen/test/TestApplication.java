package io.github.syphen.test;

import io.github.syphen.TransformerFactory;
import io.github.syphen.model.NodeContext;
import io.github.syphen.model.TransformationContext;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class TestApplication {

  public static void main(String[] args) {
    System.out.println("Test Application is running...");
    TransformerFactory transformerFactory = new TransformerFactory();
    Random1 obj1 = Random1.builder()
        .name("name")
        .value("value")
        .number(123)
        .context(Map.of())
        .build();
    TransformationContext ctx1 = TransformationContext.builder()
        .defaultOperatorType("mask")
        .nodeContext(NodeContext.builder().fieldName("root")
            .transformAllFields(true)
            .build())
        .build();
    Random1 transformedObj1 = transformerFactory.getTransformer().transform(obj1, ctx1);
    System.out.println("Transformed Object 1: " + transformedObj1);

    Random2 obj2 = Random2.builder()
        .ipA("ipA")
        .ipB(2L)
        .random1(obj1)
        .random1Set(Set.of(obj1))
        .random1Map(Map.of("random1 object", obj1))
        .random1List(List.of(obj1, obj1, obj1))
        .build();
    TransformationContext ctx2 = TransformationContext.builder()
        .defaultOperatorType("mask")
        .nodeContext(NodeContext.builder().fieldName("root")
            .transformAllFields(true)
            .transformFields(new NodeContext[]{
                NodeContext.builder().fieldName("random1").transformAllFields(true).build(),
                NodeContext.builder().fieldName("random1Set").transformAllFields(true).build(),
                NodeContext.builder().fieldName("random1List").transformAllFields(true).build()})
            .build())
        .build();
    Random2 transformedObj2 = transformerFactory.getTransformer().transform(obj2, ctx2);
    System.out.println("Transformed Object 2: " + transformedObj2);
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Random1 {

    private String name;
    private String value;
    private int number;
    private Map<String, Object> context;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Random2 {

    private String ipA;
    private Long ipB;
    private Random1 random1;
    private Set<Random1> random1Set;
    private Map<String, Random1> random1Map;
    private List<Random1> random1List;
  }

}
