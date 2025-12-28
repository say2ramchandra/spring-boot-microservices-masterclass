# Functional Interfaces in Java

## 📚 Table of Contents
1. [What are Functional Interfaces?](#what-are-functional-interfaces)
2. [Built-in Functional Interfaces](#built-in-functional-interfaces)
3. [Custom Functional Interfaces](#custom-functional-interfaces)
4. [Method References](#method-references)
5. [Function Composition](#function-composition)
6. [Use in Spring Boot](#use-in-spring-boot)
7. [Best Practices](#best-practices)

---

## What are Functional Interfaces?

A **Functional Interface** is an interface with **exactly one abstract method** (Single Abstract Method - SAM). They can have multiple default or static methods.

### @FunctionalInterface Annotation

```java
@FunctionalInterface
public interface Calculator {
    int calculate(int a, int b);
    
    // Default methods are allowed
    default void printResult(int result) {
        System.out.println("Result: " + result);
    }
    
    // Static methods are allowed
    static void info() {
        System.out.println("This is a calculator");
    }
}
```

**Why use it?**
- Can be implemented using lambda expressions
- Makes code more concise and readable
- Enables functional programming paradigm

---

## Built-in Functional Interfaces

Java provides many functional interfaces in the `java.util.function` package.

### 1. Predicate&lt;T&gt; - Boolean Tests

**Signature**: `T → boolean`

```java
@FunctionalInterface
public interface Predicate<T> {
    boolean test(T t);
}
```

**Use Cases**:
- Filtering collections
- Validation
- Conditional checks

**Examples**:
```java
// Check if number is even
Predicate<Integer> isEven = num -> num % 2 == 0;
System.out.println(isEven.test(4));  // true

// Check if string is not empty
Predicate<String> isNotEmpty = str -> !str.isEmpty();
System.out.println(isNotEmpty.test("Hello"));  // true

// Age validation
Predicate<Integer> isAdult = age -> age >= 18;
System.out.println(isAdult.test(25));  // true

// Filter list
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
List<Integer> evenNumbers = numbers.stream()
    .filter(isEven)
    .collect(Collectors.toList());
// [2, 4, 6]
```

**Predicate Composition**:
```java
Predicate<Integer> isEven = n -> n % 2 == 0;
Predicate<Integer> isGreaterThan10 = n -> n > 10;

// AND composition
Predicate<Integer> isEvenAndGreaterThan10 = isEven.and(isGreaterThan10);
System.out.println(isEvenAndGreaterThan10.test(12));  // true
System.out.println(isEvenAndGreaterThan10.test(5));   // false

// OR composition
Predicate<Integer> isEvenOrGreaterThan10 = isEven.or(isGreaterThan10);
System.out.println(isEvenOrGreaterThan10.test(11));  // true (greater than 10)
System.out.println(isEvenOrGreaterThan10.test(6));   // true (even)

// NEGATE (NOT)
Predicate<Integer> isOdd = isEven.negate();
System.out.println(isOdd.test(5));  // true
```

---

### 2. Function&lt;T, R&gt; - Transformation

**Signature**: `T → R`

```java
@FunctionalInterface
public interface Function<T, R> {
    R apply(T t);
}
```

**Use Cases**:
- Data transformation
- Type conversion
- Mapping operations

**Examples**:
```java
// Convert String to Integer
Function<String, Integer> stringToInt = str -> Integer.parseInt(str);
Integer num = stringToInt.apply("123");  // 123

// Extract length from String
Function<String, Integer> stringLength = str -> str.length();
System.out.println(stringLength.apply("Hello"));  // 5

// Convert to uppercase
Function<String, String> toUpperCase = str -> str.toUpperCase();
System.out.println(toUpperCase.apply("hello"));  // HELLO

// Square a number
Function<Integer, Integer> square = n -> n * n;
System.out.println(square.apply(5));  // 25

// Extract first character
Function<String, Character> firstChar = str -> str.charAt(0);
System.out.println(firstChar.apply("Java"));  // J

// Map transformation
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
List<Integer> nameLengths = names.stream()
    .map(stringLength)
    .collect(Collectors.toList());
// [5, 3, 7]
```

**Function Composition**:
```java
Function<Integer, Integer> multiplyBy2 = n -> n * 2;
Function<Integer, Integer> add3 = n -> n + 3;

// compose: execute second function first, then first
// (multiplyBy2.compose(add3))(5) = multiplyBy2(add3(5)) = multiplyBy2(8) = 16
Function<Integer, Integer> composedFunc = multiplyBy2.compose(add3);
System.out.println(composedFunc.apply(5));  // 16

// andThen: execute first function first, then second
// (multiplyBy2.andThen(add3))(5) = add3(multiplyBy2(5)) = add3(10) = 13
Function<Integer, Integer> chainedFunc = multiplyBy2.andThen(add3);
System.out.println(chainedFunc.apply(5));  // 13

// Real-world example: String processing pipeline
Function<String, String> trimmer = String::trim;
Function<String, String> upperCaser = String::toUpperCase;
Function<String, String> addPrefix = s -> "Hello, " + s;

Function<String, String> pipeline = trimmer
    .andThen(upperCaser)
    .andThen(addPrefix);
    
System.out.println(pipeline.apply("  john  "));  // Hello, JOHN
```

---

### 3. Consumer&lt;T&gt; - Side Effects

**Signature**: `T → void`

```java
@FunctionalInterface
public interface Consumer<T> {
    void accept(T t);
}
```

**Use Cases**:
- Printing/logging
- Saving to database
- Sending notifications
- Any operation with side effects

**Examples**:
```java
// Print to console
Consumer<String> printer = str -> System.out.println(str);
printer.accept("Hello World");  // Hello World

// Print with formatting
Consumer<Integer> printSquare = n -> System.out.println(n + " squared is " + (n * n));
printSquare.accept(5);  // 5 squared is 25

// Save to list (side effect)
List<String> savedItems = new ArrayList<>();
Consumer<String> saver = item -> savedItems.add(item.toUpperCase());
saver.accept("apple");
saver.accept("banana");
System.out.println(savedItems);  // [APPLE, BANANA]

// forEach with Consumer
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
names.forEach(printer);
// Alice
// Bob
// Charlie

// Complex consumer
Consumer<User> emailSender = user -> 
    System.out.println("Sending email to: " + user.getEmail());
Consumer<User> smsSender = user -> 
    System.out.println("Sending SMS to: " + user.getPhone());

// Consumer chaining with andThen
Consumer<User> notifier = emailSender.andThen(smsSender);
notifier.accept(new User("john@example.com", "123-456-7890"));
// Sending email to: john@example.com
// Sending SMS to: 123-456-7890
```

---

### 4. Supplier&lt;T&gt; - Data Generation

**Signature**: `() → T`

```java
@FunctionalInterface
public interface Supplier<T> {
    T get();
}
```

**Use Cases**:
- Lazy initialization
- Factory methods
- Random value generation
- Default value providers

**Examples**:
```java
// Generate random number
Supplier<Integer> randomInt = () -> new Random().nextInt(100);
System.out.println(randomInt.get());  // Random number 0-99

// Generate current timestamp
Supplier<Long> currentTime = () -> System.currentTimeMillis();
System.out.println(currentTime.get());  // Current time in milliseconds

// Generate UUID
Supplier<String> uuidGenerator = () -> UUID.randomUUID().toString();
System.out.println(uuidGenerator.get());  // Random UUID

// Default value supplier
Supplier<String> defaultName = () -> "Guest";
String name = getName().orElseGet(defaultName);

// Factory pattern
Supplier<List<String>> listFactory = () -> new ArrayList<>();
List<String> list1 = listFactory.get();
List<String> list2 = listFactory.get();  // New instance

// Lazy initialization
class ExpensiveObject {
    public ExpensiveObject() {
        System.out.println("Creating expensive object...");
    }
}

Supplier<ExpensiveObject> lazyInit = () -> new ExpensiveObject();
// Object not created yet...
ExpensiveObject obj = lazyInit.get();  // Now it's created!

// Generate sequential IDs
class IdGenerator {
    private int counter = 0;
    
    public Supplier<Integer> getSupplier() {
        return () -> ++counter;
    }
}

IdGenerator generator = new IdGenerator();
Supplier<Integer> idSupplier = generator.getSupplier();
System.out.println(idSupplier.get());  // 1
System.out.println(idSupplier.get());  // 2
System.out.println(idSupplier.get());  // 3
```

---

### 5. BiFunction&lt;T, U, R&gt; - Two Arguments

**Signature**: `(T, U) → R`

```java
@FunctionalInterface
public interface BiFunction<T, U, R> {
    R apply(T t, U u);
}
```

**Examples**:
```java
// Add two integers
BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
System.out.println(add.apply(5, 3));  // 8

// Concatenate strings
BiFunction<String, String, String> concat = (s1, s2) -> s1 + " " + s2;
System.out.println(concat.apply("Hello", "World"));  // Hello World

// Create Person object
BiFunction<String, Integer, Person> personCreator = 
    (name, age) -> new Person(name, age);
Person person = personCreator.apply("Alice", 30);

// Calculate power
BiFunction<Double, Double, Double> power = (base, exponent) -> 
    Math.pow(base, exponent);
System.out.println(power.apply(2.0, 3.0));  // 8.0

// Compare strings
BiFunction<String, String, Integer> compareStrings = 
    (s1, s2) -> s1.compareTo(s2);
System.out.println(compareStrings.apply("apple", "banana"));  // negative
```

---

### 6. BiPredicate&lt;T, U&gt; - Two Arguments Boolean Test

**Examples**:
```java
// Check if first is greater than second
BiPredicate<Integer, Integer> isGreater = (a, b) -> a > b;
System.out.println(isGreater.test(5, 3));  // true

// Check if strings are equal (ignore case)
BiPredicate<String, String> equalsIgnoreCase = 
    (s1, s2) -> s1.equalsIgnoreCase(s2);
System.out.println(equalsIgnoreCase.test("Hello", "HELLO"));  // true
```

---

### 7. BiConsumer&lt;T, U&gt; - Two Arguments Side Effect

**Examples**:
```java
// Print two values
BiConsumer<String, Integer> printInfo = 
    (name, age) -> System.out.println(name + " is " + age + " years old");
printInfo.accept("Alice", 30);  // Alice is 30 years old

// Put in map
Map<String, Integer> map = new HashMap<>();
BiConsumer<String, Integer> mapPutter = (key, value) -> map.put(key, value);
mapPutter.accept("age", 25);

// forEach on Map
map.forEach((key, value) -> 
    System.out.println(key + " = " + value));
```

---

### 8. UnaryOperator&lt;T&gt; - Same Type Transformation

**Signature**: `T → T` (Special case of Function&lt;T, T&gt;)

```java
// Square a number
UnaryOperator<Integer> square = n -> n * n;
System.out.println(square.apply(5));  // 25

// Trim string
UnaryOperator<String> trimmer = str -> str.trim();
System.out.println(trimmer.apply("  hello  "));  // hello

// Replace all vowels
UnaryOperator<String> removeVowels = 
    str -> str.replaceAll("[aeiouAEIOU]", "");
System.out.println(removeVowels.apply("Hello"));  // Hll
```

---

### 9. BinaryOperator&lt;T&gt; - Two Same Type Arguments

**Signature**: `(T, T) → T` (Special case of BiFunction&lt;T, T, T&gt;)

```java
// Sum two integers
BinaryOperator<Integer> sum = (a, b) -> a + b;
System.out.println(sum.apply(5, 3));  // 8

// Find max
BinaryOperator<Integer> max = (a, b) -> a > b ? a : b;
System.out.println(max.apply(5, 3));  // 5

// Concatenate strings
BinaryOperator<String> concat = (s1, s2) -> s1 + s2;
System.out.println(concat.apply("Hello", "World"));  // HelloWorld

// Use in reduce
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
Integer sum = numbers.stream()
    .reduce(0, sum);  // Using BinaryOperator
// 15
```

---

## Custom Functional Interfaces

You can create your own functional interfaces for domain-specific operations:

```java
@FunctionalInterface
public interface Validator<T> {
    boolean isValid(T t);
}

@FunctionalInterface
public interface Converter<F, T> {
    T convert(F from);
}

@FunctionalInterface
public interface TriFunction<T, U, V, R> {
    R apply(T t, U u, V v);
}

// Usage
Validator<String> emailValidator = email -> 
    email.contains("@") && email.contains(".");

Converter<String, Integer> stringToInt = 
    str -> Integer.parseInt(str);

TriFunction<Integer, Integer, Integer, Integer> sumThree = 
    (a, b, c) -> a + b + c;
System.out.println(sumThree.apply(1, 2, 3));  // 6
```

---

## Method References

Method references are shorthand for lambda expressions that only call an existing method.

### Types of Method References

#### 1. Static Method Reference: `ClassName::staticMethod`

```java
// Lambda
Function<String, Integer> parseInt1 = str -> Integer.parseInt(str);

// Method reference
Function<String, Integer> parseInt2 = Integer::parseInt;

System.out.println(parseInt2.apply("123"));  // 123

// More examples
BiFunction<Integer, Integer, Integer> max1 = (a, b) -> Math.max(a, b);
BiFunction<Integer, Integer, Integer> max2 = Math::max;

Consumer<String> print1 = str -> System.out.println(str);
Consumer<String> print2 = System.out::println;
```

#### 2. Instance Method Reference: `object::instanceMethod`

```java
String prefix = "Hello, ";
Function<String, String> greeter1 = name -> prefix.concat(name);
Function<String, String> greeter2 = prefix::concat;

System.out.println(greeter2.apply("Alice"));  // Hello, Alice

// List operations
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
names.forEach(System.out::println);
```

#### 3. Instance Method of Arbitrary Object: `ClassName::instanceMethod`

```java
// Lambda
Function<String, String> toUpper1 = str -> str.toUpperCase();

// Method reference
Function<String, String> toUpper2 = String::toUpperCase;

List<String> names = Arrays.asList("alice", "bob", "charlie");
names.stream()
    .map(String::toUpperCase)
    .forEach(System.out::println);
// ALICE
// BOB
// CHARLIE

// Comparator
List<String> words = Arrays.asList("banana", "apple", "cherry");
words.sort(String::compareToIgnoreCase);
```

#### 4. Constructor Reference: `ClassName::new`

```java
// Lambda
Supplier<List<String>> listSupplier1 = () -> new ArrayList<>();

// Constructor reference
Supplier<List<String>> listSupplier2 = ArrayList::new;

// With parameters
Function<String, Person> personCreator1 = name -> new Person(name);
Function<String, Person> personCreator2 = Person::new;

// Array constructor
Function<Integer, int[]> arrayCreator = int[]::new;
int[] array = arrayCreator.apply(10);  // Array of size 10
```

---

## Function Composition

Combining multiple functions to create complex transformations.

### Function Composition Example

```java
Function<String, String> trimmer = String::trim;
Function<String, String> upperCaser = String::toUpperCase;
Function<String, String> addExclamation = s -> s + "!";

// Compose: execute right to left
Function<String, String> composed = addExclamation
    .compose(upperCaser)
    .compose(trimmer);
    
System.out.println(composed.apply("  hello  "));  // HELLO!

// AndThen: execute left to right
Function<String, String> pipeline = trimmer
    .andThen(upperCaser)
    .andThen(addExclamation);
    
System.out.println(pipeline.apply("  world  "));  // WORLD!
```

### Predicate Composition

```java
Predicate<Integer> isEven = n -> n % 2 == 0;
Predicate<Integer> isPositive = n -> n > 0;
Predicate<Integer> isLessThan100 = n -> n < 100;

// AND
Predicate<Integer> isEvenAndPositive = isEven.and(isPositive);

// OR
Predicate<Integer> isEvenOrPositive = isEven.or(isPositive);

// Complex combination
Predicate<Integer> complexPredicate = isEven
    .and(isPositive)
    .and(isLessThan100);
    
System.out.println(complexPredicate.test(50));   // true
System.out.println(complexPredicate.test(150));  // false
System.out.println(complexPredicate.test(-2));   // false
```

---

## Use in Spring Boot

### 1. Repository Method Queries

```java
public interface UserRepository extends JpaRepository<User, Long> {
    // Using Predicate for filtering
    List<User> findAll(Predicate<User> predicate);
    
    // Custom query with Function
    @Query("SELECT u FROM User u WHERE u.status = :status")
    List<User> findByStatus(@Param("status") String status);
}
```

### 2. Service Layer

```java
@Service
public class UserService {
    
    // Function for transformation
    private Function<UserDTO, User> dtoToEntity = dto -> {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    };
    
    // Predicate for validation
    private Predicate<User> isValidUser = user ->
        user.getEmail() != null && 
        user.getName() != null &&
        user.getAge() >= 18;
    
    public User createUser(UserDTO dto) {
        User user = dtoToEntity.apply(dto);
        
        if (!isValidUser.test(user)) {
            throw new ValidationException("Invalid user");
        }
        
        return userRepository.save(user);
    }
}
```

### 3. Configuration

```java
@Configuration
public class AppConfig {
    
    @Bean
    public Function<String, String> uppercaseFunction() {
        return String::toUpperCase;
    }
    
    @Bean
    public Predicate<User> activeUserPredicate() {
        return user -> "ACTIVE".equals(user.getStatus());
    }
}
```

### 4. Stream Processing in Controllers

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/adults")
    public List<UserDTO> getAdults() {
        Predicate<User> isAdult = user -> user.getAge() >= 18;
        Function<User, UserDTO> toDTO = this::convertToDTO;
        
        return userRepository.findAll().stream()
            .filter(isAdult)
            .map(toDTO)
            .collect(Collectors.toList());
    }
}
```

---

## Best Practices

### 1. Keep Lambdas Short

❌ **Bad**:
```java
Function<User, String> userInfo = user -> {
    StringBuilder sb = new StringBuilder();
    sb.append("Name: ").append(user.getName());
    sb.append(", Age: ").append(user.getAge());
    sb.append(", Email: ").append(user.getEmail());
    // More lines...
    return sb.toString();
};
```

✅ **Good**:
```java
Function<User, String> userInfo = this::formatUserInfo;

private String formatUserInfo(User user) {
    return String.format("Name: %s, Age: %d, Email: %s",
        user.getName(), user.getAge(), user.getEmail());
}
```

### 2. Use Method References When Possible

❌ **Bad**:
```java
users.forEach(user -> System.out.println(user));
names.stream().map(name -> name.toUpperCase());
```

✅ **Good**:
```java
users.forEach(System.out::println);
names.stream().map(String::toUpperCase);
```

### 3. Compose Functions for Reusability

```java
// Define reusable functions
Function<String, String> trimmer = String::trim;
Function<String, String> upperCaser = String::toUpperCase;
Function<String, String> validator = str -> {
    if (str.isEmpty()) throw new IllegalArgumentException();
    return str;
};

// Compose into pipelines
Function<String, String> cleanAndValidate = trimmer
    .andThen(validator)
    .andThen(upperCaser);
```

### 4. Use Appropriate Functional Interface

```java
// Use Predicate for boolean tests
Predicate<User> isActive = user -> user.isActive();

// Use Function for transformations
Function<User, UserDTO> toDTO = user -> new UserDTO(user);

// Use Consumer for side effects
Consumer<User> sendEmail = user -> emailService.send(user);

// Use Supplier for generation
Supplier<User> createGuest = () -> new User("Guest");
```

---

## Interview Questions

**Q1: What is a functional interface?**
A: An interface with exactly one abstract method. It can have multiple default or static methods. Can be implemented using lambda expressions.

**Q2: Difference between Function and UnaryOperator?**
A: UnaryOperator is a special case of Function where input and output types are the same. `UnaryOperator<T>` is equivalent to `Function<T, T>`.

**Q3: What is the difference between andThen() and compose()?**
A: Both combine functions, but in different order:
- `f.andThen(g)`: applies f first, then g → g(f(x))
- `f.compose(g)`: applies g first, then f → f(g(x))

**Q4: Can functional interfaces have multiple methods?**
A: They can have only ONE abstract method, but can have multiple default and static methods.

**Q5: When to use Supplier vs Callable?**
A: Supplier is a functional interface that doesn't throw checked exceptions. Callable can throw checked exceptions and is typically used with ExecutorService.

---

## Summary

| Interface | Signature | Use Case | Example |
|-----------|-----------|----------|---------|
| Predicate&lt;T&gt; | T → boolean | Testing/Filtering | `age -> age >= 18` |
| Function&lt;T,R&gt; | T → R | Transformation | `str -> str.length()` |
| Consumer&lt;T&gt; | T → void | Side effects | `user -> saveToDb(user)` |
| Supplier&lt;T&gt; | () → T | Generation | `() -> new ArrayList<>()` |
| BiFunction&lt;T,U,R&gt; | (T,U) → R | Two-arg transform | `(a,b) -> a + b` |
| UnaryOperator&lt;T&gt; | T → T | Same-type transform | `n -> n * 2` |
| BinaryOperator&lt;T&gt; | (T,T) → T | Two same-type args | `(a,b) -> Math.max(a,b)` |

**Key Takeaways**:
- ✅ Functional interfaces enable lambda expressions
- ✅ Use built-in interfaces when possible
- ✅ Method references make code cleaner
- ✅ Function composition enables powerful transformations
- ✅ Essential for Stream API and modern Java development
- ✅ Heavily used in Spring Boot applications

---

**Next**: [Concurrency & Multithreading](../04-concurrency-multithreading/) →
