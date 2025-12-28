# Streams and Lambdas

> **Master functional programming with Java Streams API**

## 📚 Table of Contents

1. [What are Lambdas?](#what-are-lambdas)
2. [What are Streams?](#what-are-streams)
3. [Lambda Syntax](#lambda-syntax)
4. [Stream Operations](#stream-operations)
5. [Common Use Cases](#common-use-cases)
6. [Best Practices](#best-practices)

---

## What are Lambdas?

### Definition

**Lambda expressions** are anonymous functions that treat functionality as a method argument. They enable functional programming in Java.

### Before Lambdas (Java 7)

```java
// Anonymous inner class
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
Collections.sort(names, new Comparator<String>() {
    @Override
    public int compare(String s1, String s2) {
        return s1.compareTo(s2);
    }
});
```

### With Lambdas (Java 8+)

```java
// Lambda expression
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
Collections.sort(names, (s1, s2) -> s1.compareTo(s2));

// Even simpler with method reference
Collections.sort(names, String::compareTo);
```

**Result**: Cleaner, more readable code!

---

## What are Streams?

### Definition

A **Stream** is a sequence of elements that supports sequential and parallel aggregate operations. It's NOT a data structure but a pipeline for processing collections.

### Key Characteristics

1. **Not a data structure** - Doesn't store data
2. **Functional** - Operations don't modify source
3. **Lazy** - Intermediate operations are lazy
4. **Consumable** - Can be traversed only once

### Stream Pipeline

```
Collection → Stream → [Intermediate Operations] → Terminal Operation → Result

Example:
List → stream() → filter() → map() → sorted() → collect() → List
```

---

## Lambda Syntax

### Basic Syntax

```java
(parameters) -> expression

// Or for multi-line
(parameters) -> {
    statement1;
    statement2;
    return result;
}
```

### Examples

```java
// No parameters
() -> System.out.println("Hello")

// Single parameter (parentheses optional)
x -> x * x
(x) -> x * x

// Multiple parameters
(x, y) -> x + y

// With type declarations
(String s) -> s.length()

// Multi-line body
(a, b) -> {
    int sum = a + b;
    return sum * 2;
}
```

---

## Stream Operations

### Creating Streams

```java
// From collection
List<String> list = Arrays.asList("a", "b", "c");
Stream<String> stream = list.stream();

// From array
String[] array = {"a", "b", "c"};
Stream<String> stream = Arrays.stream(array);

// Using Stream.of()
Stream<String> stream = Stream.of("a", "b", "c");

// Infinite streams
Stream<Integer> numbers = Stream.iterate(0, n -> n + 1);
```

### Intermediate Operations (Lazy)

These return a new Stream and are not executed until a terminal operation is invoked.

#### 1. filter()
**Purpose**: Select elements based on a condition

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
List<Integer> evens = numbers.stream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());
// Result: [2, 4, 6]
```

#### 2. map()
**Purpose**: Transform each element

```java
List<String> names = Arrays.asList("alice", "bob", "charlie");
List<String> uppercase = names.stream()
    .map(String::toUpperCase)
    .collect(Collectors.toList());
// Result: [ALICE, BOB, CHARLIE]
```

#### 3. flatMap()
**Purpose**: Flatten nested structures

```java
List<List<Integer>> nested = Arrays.asList(
    Arrays.asList(1, 2),
    Arrays.asList(3, 4)
);
List<Integer> flattened = nested.stream()
    .flatMap(Collection::stream)
    .collect(Collectors.toList());
// Result: [1, 2, 3, 4]
```

#### 4. distinct()
**Purpose**: Remove duplicates

```java
List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 3, 3);
List<Integer> unique = numbers.stream()
    .distinct()
    .collect(Collectors.toList());
// Result: [1, 2, 3]
```

#### 5. sorted()
**Purpose**: Sort elements

```java
List<String> names = Arrays.asList("Charlie", "Alice", "Bob");
List<String> sorted = names.stream()
    .sorted()
    .collect(Collectors.toList());
// Result: [Alice, Bob, Charlie]

// Custom sorting
names.stream()
    .sorted(Comparator.reverseOrder())
    .collect(Collectors.toList());
```

#### 6. limit()
**Purpose**: Limit stream size

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> first3 = numbers.stream()
    .limit(3)
    .collect(Collectors.toList());
// Result: [1, 2, 3]
```

#### 7. skip()
**Purpose**: Skip first N elements

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> skipFirst2 = numbers.stream()
    .skip(2)
    .collect(Collectors.toList());
// Result: [3, 4, 5]
```

### Terminal Operations (Eager)

These trigger the stream pipeline execution and produce a result.

#### 1. collect()
**Purpose**: Collect elements into a collection

```java
// To List
List<String> list = stream.collect(Collectors.toList());

// To Set
Set<String> set = stream.collect(Collectors.toSet());

// To Map
Map<String, Integer> map = persons.stream()
    .collect(Collectors.toMap(
        Person::getName,
        Person::getAge
    ));

// Joining strings
String joined = stream.collect(Collectors.joining(", "));
```

#### 2. forEach()
**Purpose**: Perform an action for each element

```java
names.stream()
    .forEach(name -> System.out.println(name));

// Method reference
names.stream()
    .forEach(System.out::println);
```

#### 3. reduce()
**Purpose**: Reduce elements to a single value

```java
// Sum
int sum = numbers.stream()
    .reduce(0, (a, b) -> a + b);

// Or using Integer::sum
int sum = numbers.stream()
    .reduce(0, Integer::sum);

// Find max
Optional<Integer> max = numbers.stream()
    .reduce(Integer::max);
```

#### 4. count()
**Purpose**: Count elements

```java
long count = stream.count();
```

#### 5. anyMatch(), allMatch(), noneMatch()
**Purpose**: Test elements against a predicate

```java
boolean hasEven = numbers.stream()
    .anyMatch(n -> n % 2 == 0);

boolean allPositive = numbers.stream()
    .allMatch(n -> n > 0);

boolean noNegative = numbers.stream()
    .noneMatch(n -> n < 0);
```

#### 6. findFirst(), findAny()
**Purpose**: Find elements

```java
Optional<String> first = names.stream()
    .findFirst();

Optional<String> any = names.stream()
    .findAny();
```

#### 7. min(), max()
**Purpose**: Find minimum or maximum

```java
Optional<Integer> min = numbers.stream()
    .min(Integer::compareTo);

Optional<Integer> max = numbers.stream()
    .max(Integer::compareTo);
```

---

## Common Use Cases

### Use Case 1: Filtering and Transforming

```java
// Get uppercase names of adults
List<Person> people = getPeople();
List<String> adultNames = people.stream()
    .filter(p -> p.getAge() >= 18)
    .map(Person::getName)
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

### Use Case 2: Grouping

```java
// Group people by age
Map<Integer, List<Person>> byAge = people.stream()
    .collect(Collectors.groupingBy(Person::getAge));

// Group by age and count
Map<Integer, Long> countByAge = people.stream()
    .collect(Collectors.groupingBy(
        Person::getAge,
        Collectors.counting()
    ));
```

### Use Case 3: Partitioning

```java
// Partition into adults and minors
Map<Boolean, List<Person>> partitioned = people.stream()
    .collect(Collectors.partitioningBy(p -> p.getAge() >= 18));

List<Person> adults = partitioned.get(true);
List<Person> minors = partitioned.get(false);
```

### Use Case 4: Statistical Operations

```java
IntSummaryStatistics stats = numbers.stream()
    .mapToInt(Integer::intValue)
    .summaryStatistics();

System.out.println("Count: " + stats.getCount());
System.out.println("Sum: " + stats.getSum());
System.out.println("Average: " + stats.getAverage());
System.out.println("Max: " + stats.getMax());
System.out.println("Min: " + stats.getMin());
```

### Use Case 5: Flattening

```java
// Get all products from all orders
List<Order> orders = getOrders();
List<Product> allProducts = orders.stream()
    .flatMap(order -> order.getProducts().stream())
    .collect(Collectors.toList());
```

---

## Best Practices

### ✅ DO:

#### 1. Use Method References When Possible

```java
// Good - method reference
names.stream()
    .map(String::toUpperCase)
    .forEach(System.out::println);

// Acceptable but verbose
names.stream()
    .map(s -> s.toUpperCase())
    .forEach(s -> System.out.println(s));
```

#### 2. Use Primitive Streams for Performance

```java
// Good - primitive stream
int sum = numbers.stream()
    .mapToInt(Integer::intValue)
    .sum();

// Slower - boxing/unboxing
int sum = numbers.stream()
    .reduce(0, Integer::sum);
```

#### 3. Chain Operations Logically

```java
// Good - readable chain
List<String> result = people.stream()
    .filter(p -> p.getAge() >= 18)
    .map(Person::getName)
    .sorted()
    .collect(Collectors.toList());
```

#### 4. Use Collectors Wisely

```java
// Good - using Collectors
Map<String, List<Person>> byCity = people.stream()
    .collect(Collectors.groupingBy(Person::getCity));

// Good - custom collector for complex cases
```

---

### ❌ DON'T:

#### 1. Don't Reuse Streams

```java
// BAD - stream already consumed
Stream<String> stream = names.stream();
stream.forEach(System.out::println);
stream.forEach(System.out::println);  // IllegalStateException!

// GOOD - create new stream
names.stream().forEach(System.out::println);
names.stream().forEach(System.out::println);
```

#### 2. Don't Use Streams for Everything

```java
// BAD - overkill for simple loop
list.stream()
    .forEach(item -> process(item));

// GOOD - simple for loop is clearer
for (Item item : list) {
    process(item);
}
```

#### 3. Don't Modify Source in Stream

```java
// BAD - modifying source
List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3));
numbers.stream()
    .forEach(n -> numbers.add(n * 2));  // ConcurrentModificationException!

// GOOD - create new collection
List<Integer> doubled = numbers.stream()
    .map(n -> n * 2)
    .collect(Collectors.toList());
```

#### 4. Don't Use Parallel Streams Blindly

```java
// BAD - parallel might be slower for small collections
List<Integer> small = Arrays.asList(1, 2, 3);
small.parallelStream()  // Overhead > Benefit
    .forEach(System.out::println);

// GOOD - parallel for large, CPU-intensive operations
List<Integer> large = getLargeList();  // 1 million items
large.parallelStream()
    .filter(this::expensiveOperation)
    .collect(Collectors.toList());
```

---

## Performance Considerations

### When to Use Streams

✅ **Good for**:
- Large collections
- Complex transformations
- Filtering and mapping
- Parallel processing of independent operations

❌ **Avoid for**:
- Small collections (< 100 items)
- Simple iterations
- Operations with side effects
- When readability suffers

### Optimization Tips

```java
// TIP 1: Filter early
// Good
stream.filter(expensive).map(transform).collect();

// Bad - processes all before filtering
stream.map(transform).filter(expensive).collect();

// TIP 2: Use short-circuit operations
// Stops at first match
boolean exists = stream.anyMatch(condition);

// TIP 3: Limit when possible
stream.limit(10).forEach(process);  // Only processes 10
```

---

## Demo Projects

Explore these hands-on demos:

1. **[demo-lambda-basics](demo-lambda-basics/)** - Lambda syntax and usage
2. **[demo-stream-operations](demo-stream-operations/)** - All stream operations
3. **[demo-data-processing](demo-data-processing/)** - Real-world data processing
4. **[demo-parallel-streams](demo-parallel-streams/)** - Parallel processing

---

## Interview Questions

### Q1: Difference between map() and flatMap()?
**A:** `map()` transforms each element 1-to-1. `flatMap()` transforms each element to a stream and flattens the results.

### Q2: What's lazy evaluation in streams?
**A:** Intermediate operations are not executed until a terminal operation is called. This allows optimization.

### Q3: When to use parallel streams?
**A:** For large datasets with CPU-intensive, independent operations. Not for small data or I/O operations.

### Q4: Can you reuse a stream?
**A:** No, streams can only be consumed once. Create a new stream for each operation.

---

## Next Steps

Continue to:
- **[Functional Interfaces →](../03-functional-interfaces/)** - Deep dive into functional programming

---

_Streams make your code functional, elegant, and powerful! 🚀_
