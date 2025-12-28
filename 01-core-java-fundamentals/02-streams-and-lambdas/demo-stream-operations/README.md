# Stream Operations Demo

> **Comprehensive demonstration of Java Stream API**

## 🎯 Learning Objectives

After completing this demo, you will understand:

- How to create streams from various sources
- All intermediate operations (filter, map, flatMap, distinct, sorted, etc.)
- All terminal operations (collect, forEach, reduce, count, etc.)
- Real-world stream usage patterns
- Performance considerations with parallel streams

---

## 📂 Project Structure

```
demo-stream-operations/
├── pom.xml
├── README.md
└── src/
    └── main/
        └── java/
            └── com/
                └── masterclass/
                    └── streams/
                        └── StreamOperationsDemo.java
```

---

## 🚀 How to Run

### Option 1: Using Maven

```bash
cd demo-stream-operations
mvn clean compile exec:java
```

### Option 2: From IDE

1. Open project in your IDE
2. Navigate to `StreamOperationsDemo.java`
3. Run the main method

---

## 📋 What This Demo Covers

### Part 1: Creating Streams
- From Collections
- From Arrays
- Using Stream.of()
- Infinite Streams
- IntStream ranges

### Part 2: Intermediate Operations
- `filter()` - Filtering elements
- `map()` - Transforming elements
- `flatMap()` - Flattening nested structures
- `distinct()` - Removing duplicates
- `sorted()` - Sorting with/without comparators
- `limit()` - Limiting stream size
- `skip()` - Skipping elements
- `peek()` - Debugging streams

### Part 3: Terminal Operations
- `collect()` - Collecting to collections
- `forEach()` - Iteration
- `reduce()` - Reduction operations
- `count()` - Counting elements
- `anyMatch()`, `allMatch()`, `noneMatch()` - Matching
- `findFirst()`, `findAny()` - Finding elements
- `min()`, `max()` - Finding extremes

### Part 4: Real-World Example
Complete Employee Management System demonstrating:
- Filtering by department
- Salary calculations
- Grouping and aggregation
- Statistical analysis
- Top N queries

### Part 5: Advanced Operations
- Complex pipelines
- FlatMap use cases
- Custom collectors
- Parallel streams
- takeWhile/dropWhile (Java 9+)

---

## 💡 Key Takeaways

### Stream Pipeline Structure

```
Source → [Intermediate Operations] → Terminal Operation → Result

Example:
list.stream()
    .filter(...)      // Intermediate
    .map(...)         // Intermediate
    .sorted()         // Intermediate
    .collect(...)     // Terminal
```

### Lazy vs Eager Evaluation

- **Intermediate operations** are **lazy** - not executed until terminal operation
- **Terminal operations** are **eager** - trigger pipeline execution

### Performance Tips

1. **Filter early** - Reduce data size before expensive operations
2. **Use primitive streams** - `IntStream`, `LongStream`, `DoubleStream` for better performance
3. **Parallel streams** - Only for large datasets with CPU-intensive operations
4. **Short-circuit operations** - Use `anyMatch()`, `findFirst()` when appropriate

---

## 🔍 Sample Output

```
================================================================================
JAVA STREAMS API - COMPREHENSIVE DEMO
================================================================================

--------------------------------------------------------------------------------
PART 1: CREATING STREAMS
--------------------------------------------------------------------------------

1. From Collection:
   - Apple
   - Banana
   - Cherry

2. From Array:
   - Red
   - Green
   - Blue

...

--------------------------------------------------------------------------------
PART 4: REAL-WORLD EXAMPLE - EMPLOYEE MANAGEMENT SYSTEM
--------------------------------------------------------------------------------

1. Engineering Department Employees:
   - Alice Johnson (ID: 1)
   - Charlie Brown (ID: 3)
   - Eve Davis (ID: 5)
   - Henry Wilson (ID: 8)

2. High Earners (>$70k):
   - Alice Johnson
   - Charlie Brown
   - Frank Miller
   - Henry Wilson

3. Average Salary by Department:
   - Engineering: $84500.00
   - Marketing: $66500.00
   - HR: $60000.00
   - Management: $120000.00

...
```

---

## 🎓 Practice Exercises

Try modifying the code to:

1. Find the youngest employee in the Engineering department
2. Calculate total salary for employees under age 30
3. Create a list of all unique departments
4. Find employees whose names start with a vowel
5. Sort employees by salary descending, then by age ascending

---

## 📚 Related Topics

- [Lambda Expressions](../01-lambda-basics/)
- [Functional Interfaces](../03-functional-interfaces/)
- [Optional API](../../03-optional-api/)

---

## 🐛 Troubleshooting

**Problem**: Stream has already been operated upon or closed

**Solution**: Create a new stream for each operation. Streams can only be consumed once.

```java
// BAD
Stream<String> stream = list.stream();
stream.forEach(System.out::println);
stream.count();  // IllegalStateException!

// GOOD
list.stream().forEach(System.out::println);
list.stream().count();
```

---

**Requirement**: Java 17+

_Master streams, master functional programming! 🚀_
