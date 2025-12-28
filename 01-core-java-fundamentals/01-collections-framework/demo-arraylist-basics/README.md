# ArrayList Basics Demo

> **Hands-on demonstration of ArrayList operations with real-world examples**

## 📚 What This Demo Demonstrates

This demo covers:
- ✅ Creating ArrayList in different ways
- ✅ Adding, accessing, modifying, and removing elements
- ✅ Searching and sorting operations
- ✅ Different iteration techniques
- ✅ Real-world use case: Task Manager

## 🎯 Learning Objectives

After completing this demo, you will understand:
- When to use ArrayList vs other collections
- Performance characteristics of ArrayList operations
- Best practices for ArrayList usage
- How to apply ArrayList in real applications

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.8+
- Basic Java knowledge

## 🚀 How to Run

### Option 1: Using Maven
```bash
# Navigate to demo directory
cd demo-arraylist-basics

# Clean and compile
mvn clean compile

# Run the application
mvn exec:java
```

### Option 2: Using IDE
1. Open the project in your IDE (IntelliJ IDEA, Eclipse, VS Code)
2. Navigate to `ArrayListDemo.java`
3. Right-click and select "Run" or press `Shift+F10` (IntelliJ)

### Option 3: Command Line (after compilation)
```bash
# Compile
javac -d target/classes src/main/java/com/masterclass/collections/ArrayListDemo.java

# Run
java -cp target/classes com.masterclass.collections.ArrayListDemo
```

## 📊 Expected Output

You will see 9 examples demonstrating:

```
============================================================
        ArrayList Basics Demonstration
============================================================

📚 Example 1: Creating ArrayList
----------------------------------------
1. Default constructor: []
2. With capacity 100: []
3. From collection: [Apple, Banana, Cherry]
4. Immutable list: [A, B, C]

💡 Tip: Use initial capacity when size is known for better performance!

============================================================

➕ Example 2: Adding Elements
----------------------------------------
After adding 3 fruits: [Apple, Banana, Cherry]
After adding at index 1: [Apple, Avocado, Banana, Cherry]
After adding collection: [Apple, Avocado, Banana, Cherry, Date, Elderberry]
After adding at start: [Apricot, Acai, Apple, Avocado, Banana, Cherry, Date, Elderberry]

💡 Adding at end is O(1), adding in middle is O(n)!

... (more examples follow)
```

## 🔑 Key Concepts Demonstrated

### 1. Creating ArrayList

```java
// Default constructor
List<String> list1 = new ArrayList<>();

// With initial capacity (performance optimization)
List<String> list2 = new ArrayList<>(100);

// From another collection
List<String> list3 = new ArrayList<>(List.of("A", "B", "C"));
```

**Why it matters**: Using initial capacity prevents resizing overhead when you know the approximate size.

### 2. Adding Elements

```java
list.add("element");              // Add at end: O(1)*
list.add(1, "element");           // Add at index: O(n)
list.addAll(anotherList);         // Add collection: O(m)
```

**Performance Tip**: Adding at the end is fast (amortized O(1)). Adding in the middle requires shifting elements.

### 3. Accessing Elements

```java
String item = list.get(0);        // Get by index: O(1)
boolean exists = list.contains("item");  // Contains: O(n)
int size = list.size();           // Size: O(1)
```

**Key Point**: ArrayList excels at random access by index!

### 4. Modifying Elements

```java
list.set(0, "newValue");          // Replace: O(1)
list.replaceAll(String::toUpperCase);  // Transform all: O(n)
```

### 5. Removing Elements

```java
list.remove(0);                   // Remove by index: O(n)
list.remove("item");              // Remove by object: O(n)
list.removeIf(item -> condition); // Conditional removal: O(n)
list.clear();                     // Remove all: O(n)
```

**Important**: Removal requires shifting elements, making it expensive.

### 6. Searching

```java
int index = list.indexOf("item");      // First occurrence: O(n)
int last = list.lastIndexOf("item");   // Last occurrence: O(n)
boolean has = list.contains("item");   // Check existence: O(n)
```

### 7. Sorting

```java
Collections.sort(list);                    // Natural order
list.sort(Collections.reverseOrder());     // Reverse order
list.sort((a, b) -> a.compareTo(b));      // Custom comparator
```

### 8. Iteration Techniques

```java
// Enhanced for loop (most readable)
for (String item : list) { }

// forEach with lambda (functional style)
list.forEach(item -> System.out.println(item));

// Stream API (for transformations)
list.stream()
    .filter(item -> item.startsWith("A"))
    .forEach(System.out::println);

// Iterator (safe for removal during iteration)
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    String item = it.next();
    if (condition) it.remove();  // Safe removal
}
```

### 9. Real-World Example: Task Manager

The demo includes a practical Task Manager that shows:
- Dynamic task addition
- Task completion (removal)
- Task display (iteration)
- Pending task count

This mirrors real applications like:
- Todo apps
- Order processing systems
- Queue management

## 📈 Performance Characteristics

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| `add(E)` | O(1) amortized | O(n) when resizing needed |
| `add(index, E)` | O(n) | Must shift elements |
| `get(index)` | O(1) | Direct array access |
| `set(index, E)` | O(1) | Direct replacement |
| `remove(index)` | O(n) | Must shift elements |
| `remove(Object)` | O(n) | Must search + shift |
| `contains(Object)` | O(n) | Linear search |
| `size()` | O(1) | Stored as field |

**Key Takeaway**: ArrayList is excellent for read-heavy operations with random access!

## 🎓 Exercises

Try these modifications to deepen your understanding:

### Exercise 1: Student Management System
Create a `Student` class and build a system to:
- Add students with name and grade
- Remove students by name
- Sort students by grade
- Find students with grade above average

### Exercise 2: Shopping Cart
Implement a shopping cart using ArrayList:
- Add/remove products
- Calculate total price
- Apply discounts
- Display cart contents

### Exercise 3: Performance Testing
Compare performance of:
- Adding 10,000 elements at the end
- Adding 10,000 elements at index 0
- Accessing 10,000 elements by index
- Searching for 10,000 elements

### Exercise 4: Custom Implementation
Try implementing a simple version of ArrayList:
- Fixed-size array backing
- Dynamic resizing when full
- Basic add/get/remove operations

## 🐛 Common Mistakes to Avoid

### ❌ Mistake 1: Modifying list during iteration
```java
// WRONG - ConcurrentModificationException
for (String item : list) {
    if (item.equals("remove")) {
        list.remove(item);  // ERROR!
    }
}

// CORRECT - Use Iterator
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    if (it.next().equals("remove")) {
        it.remove();  // Safe
    }
}
```

### ❌ Mistake 2: Using == for comparison
```java
// WRONG
if (list1 == list2) { }

// CORRECT
if (list1.equals(list2)) { }
```

### ❌ Mistake 3: Not using generics
```java
// WRONG - No type safety
List list = new ArrayList();
list.add("String");
list.add(123);  // Allowed but dangerous

// CORRECT
List<String> list = new ArrayList<>();
// list.add(123);  // Compile error - type safe!
```

### ❌ Mistake 4: Unnecessary resizing
```java
// WRONG - Will resize multiple times
List<String> list = new ArrayList<>();
for (int i = 0; i < 10000; i++) {
    list.add("item" + i);
}

// CORRECT - Pre-allocate capacity
List<String> list = new ArrayList<>(10000);
for (int i = 0; i < 10000; i++) {
    list.add("item" + i);
}
```

## 💡 Best Practices

✅ **Program to interfaces**
```java
List<String> list = new ArrayList<>();  // Not ArrayList<String>
```

✅ **Use initial capacity when size is known**
```java
List<String> list = new ArrayList<>(1000);
```

✅ **Use appropriate collection type**
- ArrayList: Random access, read-heavy
- LinkedList: Frequent insertions/deletions at start
- Use Set if uniqueness required

✅ **Make defensive copies**
```java
public List<String> getItems() {
    return new ArrayList<>(items);  // Return copy, not reference
}
```

✅ **Use immutable lists when appropriate**
```java
List<String> immutable = List.of("A", "B", "C");
```

## 🔗 Related Concepts

- **[LinkedList](../demo-linkedlist/)** - When to use LinkedList instead
- **[Performance Comparison](../demo-collection-comparison/)** - Benchmark all collections
- **[Streams and Lambdas](../../02-streams-and-lambdas/)** - Process lists functionally

## 📚 Additional Resources

- [ArrayList JavaDoc](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/ArrayList.html)
- [Collections Framework Guide](https://docs.oracle.com/javase/tutorial/collections/)
- [Effective Java - Item 64: Refer to objects by their interfaces](https://www.oracle.com/java/technologies/javase/effective-java.html)

## ❓ Quiz Yourself

1. What is the time complexity of `get(index)` in ArrayList?
2. Why is adding at the end faster than adding at the start?
3. When would LinkedList be better than ArrayList?
4. What happens internally when ArrayList reaches capacity?
5. Can ArrayList contain null elements?

<details>
<summary>Click for answers</summary>

1. O(1) - direct array access
2. Adding at end doesn't require shifting elements; adding at start shifts all elements
3. When you frequently insert/delete at the beginning or middle
4. It creates a new array (typically 1.5x size) and copies elements
5. Yes, ArrayList can contain null elements

</details>

---

**Next Step**: Try the exercises, then explore [HashMap Cache Demo →](../demo-hashmap-cache/)

_Practice makes perfect! Run this demo multiple times and experiment with the code._
