# Functional Interfaces Demo

## Overview
Comprehensive demonstration of all Java functional interfaces with practical examples.

## What You'll Learn
- ✅ **Predicate&lt;T&gt;** - Boolean tests and filtering
- ✅ **Function&lt;T,R&gt;** - Data transformation
- ✅ **Consumer&lt;T&gt;** - Side effects
- ✅ **Supplier&lt;T&gt;** - Data generation
- ✅ **BiFunction, BiPredicate, BiConsumer** - Two-argument operations
- ✅ **UnaryOperator, BinaryOperator** - Same-type transformations
- ✅ **Method References** - Clean code shortcuts
- ✅ **Function Composition** - Combining operations

## Running the Demo

```bash
cd 03-functional-interfaces/demo-functional-interfaces
mvn clean compile exec:java
```

## Demo Structure

### Part 1: Predicate (Boolean Tests)
- Simple predicates for filtering
- Predicate composition (AND, OR, NEGATE)
- Complex filters
- Real-world employee filtering

### Part 2: Function (Transformation)
- Type conversion
- Data extraction
- Function composition (andThen, compose)
- Transformation pipelines

### Part 3: Consumer (Side Effects)
- Printing and logging
- Data mutation
- Consumer chaining
- Multiple side effects

### Part 4: Supplier (Data Generation)
- Random data generation
- Lazy initialization
- Factory patterns
- ID generation

### Part 5: BiFunction (Two Arguments)
- Object creation
- Calculations
- String operations
- Complex transformations

### Part 6-9: Other Functional Interfaces
- BiPredicate - Two-argument tests
- BiConsumer - Two-argument side effects
- UnaryOperator - Same-type transformations
- BinaryOperator - Two same-type arguments

### Part 10: Method References
- Static method references
- Instance method references
- Constructor references
- Using with streams

### Part 11: Function Composition
- AndThen (left-to-right)
- Compose (right-to-left)
- Complex pipelines

### Part 12: Real-World Scenarios
- Employee processing pipeline
- Salary analysis
- Department grouping
- Bonus calculation

## Key Concepts Demonstrated

### 1. Predicate Composition
```java
Predicate<Employee> isHighEarner = emp -> emp.getSalary() > 70000;
Predicate<Employee> isActive = Employee::isActive;

// Combine with AND
Predicate<Employee> highEarnerAndActive = isHighEarner.and(isActive);

// Combine with OR
Predicate<Employee> itOrSenior = isIT.or(isSenior);

// Negate
Predicate<Employee> notHighEarner = isHighEarner.negate();
```

### 2. Function Chaining
```java
Function<String, String> trimmer = String::trim;
Function<String, String> upperCaser = String::toUpperCase;

// Chain with andThen
Function<String, String> pipeline = trimmer.andThen(upperCaser);

// Chain with compose
Function<String, String> composed = upperCaser.compose(trimmer);
```

### 3. Consumer Chaining
```java
Consumer<Employee> printName = emp -> System.out.println(emp.getName());
Consumer<Employee> raiseSalary = emp -> emp.setSalary(emp.getSalary() * 1.10);

// Chain consumers
Consumer<Employee> processEmployee = printName.andThen(raiseSalary);
```

### 4. Method References
```java
// Static method
Function<String, Integer> parseInt = Integer::parseInt;

// Instance method
Function<String, String> greeter = prefix::concat;

// Instance method of arbitrary object
Function<String, String> toUpper = String::toUpperCase;

// Constructor
Supplier<ArrayList<String>> listSupplier = ArrayList::new;
```

## Expected Output

The demo prints detailed output for each part, showing:
- How each functional interface works
- Composition and chaining examples
- Real-world use cases
- Employee data processing

Sample output:
```
╔═══════════════════════════════════════════╗
║  Functional Interfaces Demo              ║
╚═══════════════════════════════════════════╝

==================================================
PART 1: Predicate<T> - Boolean Tests
==================================================

1. Simple Predicates:
   High earners (>70k):
   - Alice Johnson: $75000.0
   - Charlie Brown: $82000.0
   ...

2. Predicate Composition:
   High earners who are active:
   - Alice Johnson
   - Charlie Brown
   ...
```

## Real-World Applications

### 1. Data Validation
```java
Predicate<Employee> isValid = emp ->
    emp.getSalary() > 0 && 
    emp.getAge() >= 18 && 
    emp.isActive();

employees.stream()
    .filter(isValid)
    .forEach(processEmployee);
```

### 2. Data Transformation
```java
Function<Employee, EmployeeDTO> toDTO = emp ->
    new EmployeeDTO(emp.getName(), emp.getDepartment());

List<EmployeeDTO> dtos = employees.stream()
    .map(toDTO)
    .collect(Collectors.toList());
```

### 3. Side Effects (Logging, Notifications)
```java
Consumer<Employee> sendWelcomeEmail = emp ->
    emailService.send(emp.getEmail(), "Welcome!");

employees.stream()
    .filter(emp -> emp.isActive())
    .forEach(sendWelcomeEmail);
```

### 4. Factory Patterns
```java
Supplier<Connection> connectionSupplier = () ->
    DriverManager.getConnection(url, user, pass);

Connection conn = connectionSupplier.get();
```

## Best Practices Shown

1. ✅ Use method references when possible
2. ✅ Compose functions for reusability
3. ✅ Keep lambdas short and readable
4. ✅ Use appropriate functional interface
5. ✅ Chain operations for clarity
6. ✅ Avoid side effects in Functions and Predicates

## Common Interview Questions

**Q: What's the difference between Function and UnaryOperator?**
A: UnaryOperator is Function where input and output types are the same.

**Q: When to use andThen vs compose?**
A: andThen executes left-to-right, compose executes right-to-left.

**Q: Can Predicate throw checked exceptions?**
A: No, you need to handle checked exceptions inside the lambda.

**Q: What's the benefit of method references?**
A: More concise, easier to read, and can improve performance.

## Integration with Spring Boot

These patterns are used extensively in Spring Boot:
- Repository method queries (Predicate)
- DTO mapping (Function)
- Event listeners (Consumer)
- Bean factories (Supplier)

## Next Steps

1. Run the demo and study the output
2. Modify the examples to experiment
3. Try creating your own functional interfaces
4. Practice function composition
5. Move on to [Concurrency & Multithreading](../../04-concurrency-multithreading/)

---

**💡 Tip**: Functional interfaces are the foundation of modern Java and Stream API. Master them for clean, maintainable code!
