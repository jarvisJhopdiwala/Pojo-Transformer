# POJO Transformer

A flexible and extensible Java library for transforming POJOs (Plain Old Java Objects) using custom operators and field-level transformation rules. The library provides a strategy-based approach to apply transformations on JSON representations of Java objects.

## Features

- **Custom Operators**: Define and register custom transformation operators using annotations
- **Field-Level Transformations**: Apply transformations to specific fields or entire objects
- **Nested Object Support**: Handle complex nested POJO structures and collections
- **Strategy Pattern**: Pluggable transformation strategies for different scenarios
- **Caching**: Built-in caching for POJO schema reflection to improve performance
- **Type Safety**: Maintains type information during transformations
- **Flexible Configuration**: Support for conditional field transformations and exclusions

## Quick Start

### 1. Add Dependency

```xml
<dependency>
    <groupId>io.github.syphen</groupId>
    <artifactId>pojo-transformer</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. Create a Custom Operator

```java
@RegisterOperator("UPPERCASE")
public class UppercaseOperator extends BaseOperator {
    @Override
    public JsonNode apply(JsonNode input, Class<?> inputClass) {
        if (input == null || !input.isTextual()) {
            return input;
        }
        return new TextNode(input.asText().toUpperCase());
    }
}
```

### 3. Initialize the Transformer

```java
// Scan entire classpath for operators
TransformerFactory factory = new TransformerFactory();
Transformer transformer = factory.getTransformer();

// Or scan specific packages
TransformerFactory factory = new TransformerFactory("com.yourcompany.operators");
Transformer transformer = factory.getTransformer();
```

### 4. Transform Your Objects

```java
public class Person {
    private String name;
    private String email;
    private List<String> tags;
    // constructors, getters, setters...
}

// Create transformation context
NodeContext nodeContext = NodeContext.builder()
    .fieldName("name")
    .operateType("UPPERCASE")
    .build();

TransformationContext context = TransformationContext.builder()
    .defaultOperatorType("UPPERCASE")
    .nodeContext(nodeContext)
    .build();

// Transform the object
Person person = new Person("john doe", "john@example.com", List.of("developer", "java"));
Person transformed = transformer.transform(person, context);
// Result: name = "JOHN DOE"
```

## Core Concepts

### Operators

Operators define the transformation logic for individual values. They extend `BaseOperator` and are registered using the `@RegisterOperator` annotation.

```java
@RegisterOperator("TRIM")
public class TrimOperator extends BaseOperator {
    @Override
    public JsonNode apply(JsonNode input, Class<?> inputClass) {
        if (input != null && input.isTextual()) {
            return new TextNode(input.asText().trim());
        }
        return input;
    }
}
```

### Transformation Context

The `TransformationContext` defines what transformations to apply and where:

```java
TransformationContext context = TransformationContext.builder()
    .defaultOperatorType("TRIM")  // Default operator for all fields
    .nodeContext(NodeContext.builder()
        .fieldName("email")       // Target specific field
        .operateType("LOWERCASE") // Use specific operator
        .build())
    .build();
```

### Node Context Options

#### Transform All Fields
```java
NodeContext.builder()
    .transformAllFields(true)
    .operateType("TRIM")
    .build();
```

#### Transform Specific Fields
```java
NodeContext.builder()
    .transformFields(new NodeContext[]{
        NodeContext.builder().fieldName("name").operateType("UPPERCASE").build(),
        NodeContext.builder().fieldName("email").operateType("LOWERCASE").build()
    })
    .build();
```

#### Skip Specific Fields
```java
NodeContext.builder()
    .transformAllFields(true)
    .operateType("TRIM")
    .skipFields(new NodeContext[]{
        NodeContext.builder().fieldName("id").build(),
        NodeContext.builder().fieldName("createdAt").build()
    })
    .build();
```

## Advanced Usage

### Nested Objects

The library automatically handles nested POJO transformations:

```java
public class Company {
    private String name;
    private Person ceo;        // Nested object
    private List<Person> employees; // Collection of objects
}

// Transform nested structures
NodeContext nestedContext = NodeContext.builder()
    .fieldName("ceo")
    .transformFields(new NodeContext[]{
        NodeContext.builder().fieldName("name").operateType("UPPERCASE").build()
    })
    .build();
```

### Collections

Collections are handled automatically. The transformation is applied to each element:

```java
// This will apply UPPERCASE to each string in a List<String>
NodeContext listContext = NodeContext.builder()
    .fieldName("tags")
    .operateType("UPPERCASE")
    .build();
```

### Custom Type Operators

Create operators that work with specific types:

```java
@RegisterOperator("FORMAT_DATE")
public class DateFormatterOperator extends BaseOperator {
    @Override
    public JsonNode apply(JsonNode input, Class<?> inputClass) {
        if (input != null && input.isTextual()) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(input.asText());
                String formatted = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
                return new TextNode(formatted);
            } catch (Exception e) {
                return input; // Return original on error
            }
        }
        return input;
    }
}
```

## Architecture

The library follows a layered architecture:

1. **Transformer**: Main entry point for transformations
2. **NodeTransformer**: Delegates to appropriate processors based on JSON node type
3. **Processors**: Handle different JSON node types (Object, Array)
4. **Strategies**: Determine how to apply transformations (Direct value vs Nested)
5. **Operators**: Contain the actual transformation logic
6. **Cache**: Optimizes reflection-based field inspection

### Key Components

- **TransformerFactory**: Discovers and initializes operators
- **OperatorRegistry**: Manages registered operators
- **PojoSchemaCache**: Caches field type information for performance
- **ProcessorSelector**: Chooses between object and array processors
- **StrategySelector**: Chooses between direct value and nested transformations

## Error Handling

The library provides comprehensive error handling with specific error codes:

```java
try {
    Person transformed = transformer.transform(person, context);
} catch (DataTransformationException e) {
    System.err.println("Error: " + e.getCode());
    System.err.println("Status: " + e.getStatus());
    System.err.println("Context: " + e.getContext());
}
```

### Error Codes

- `FIELD_NOT_EXIST`: Referenced field doesn't exist in the class
- `OPERATOR_NULL_ERROR`: Operator not found in registry
- `TRANSFORMATION_ERROR`: General transformation failure
- `INITIALIZATION_OPERATOR_ERROR`: Operator instantiation failed
- `DUPLICATE_OPERATOR_KEY_ERROR`: Duplicate operator keys found

## Performance Considerations

- **Caching**: Field type information is cached using Caffeine cache
- **Parallel Processing**: Operator discovery uses parallel streams
- **Minimal Reflection**: Reflection is minimized and cached
- **Immutable Collections**: Internal collections are made immutable for thread safety

## Thread Safety

The library is designed to be thread-safe:
- Immutable transformation contexts
- Thread-safe caching
- Concurrent operator discovery
- No shared mutable state in core components

## Requirements

- Java 17 or higher
- Jackson 2.15.2+
- SLF4J for logging

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Examples

Check out the `examples/` directory for more comprehensive examples including:

- String manipulation operators
- Date/time transformations
- Custom validation operators
- Complex nested object transformations
- Performance benchmarks
