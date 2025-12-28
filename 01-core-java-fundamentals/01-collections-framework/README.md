# Collections Framework

> **Master Java Collections - The foundation of data manipulation in Spring Boot**

## 📚 Table of Contents

1. [Introduction](#introduction)
2. [Collection Hierarchy](#collection-hierarchy)
3. [List Interface](#list-interface)
4. [Set Interface](#set-interface)
5. [Map Interface](#map-interface)
6. [Choosing the Right Collection](#choosing-the-right-collection)
7. [Real-World Scenarios](#real-world-scenarios)

---

## Introduction

### What is the Collections Framework?

The Java Collections Framework is a unified architecture for representing and manipulating collections of objects. It provides:

- **Interfaces**: Abstract data types (List, Set, Map, Queue)
- **Implementations**: Concrete classes (ArrayList, HashSet, HashMap)
- **Algorithms**: Methods for searching, sorting, and manipulating collections

### Why Do We Need Collections?

**Problem**: Arrays have fixed size and limited functionality
```java
String[] users = new String[5]; // Fixed size!
// What if we need to add more users?
```

**Solution**: Collections provide dynamic, flexible data structures
```java
List<String> users = new ArrayList<>(); // Dynamic size!
users.add("John");
users.add("Jane");
// Add as many as needed
```

---

## Collection Hierarchy

```
                    Collection<E>
                         |
        +----------------+----------------+
        |                |                |
     List<E>          Set<E>          Queue<E>
        |                |                |
   ArrayList         HashSet        LinkedList
   LinkedList       TreeSet        PriorityQueue
     Vector         LinkedHashSet
    
    
                    Map<E>  (Not part of Collection)
                      |
              +-------+-------+
              |       |       |
          HashMap  TreeMap  LinkedHashMap
          Hashtable
```

---

## List Interface

### Definition
An **ordered collection** (also known as a sequence) that allows **duplicate elements**. Users can access elements by their integer index (position).

### Common Implementations

#### 1. ArrayList
- **Backed by**: Resizable array
- **Performance**: Fast random access O(1), slow insertion/deletion in middle O(n)
- **Use when**: You need fast access by index and mostly add at the end

```java
List<String> arrayList = new ArrayList<>();
arrayList.add("Apple");        // O(1) - amortized
arrayList.get(0);              // O(1)
arrayList.add(1, "Banana");    // O(n) - shifts elements
```

#### 2. LinkedList
- **Backed by**: Doubly-linked list
- **Performance**: Slow random access O(n), fast insertion/deletion O(1)
- **Use when**: You frequently insert/delete elements at beginning or middle

```java
List<String> linkedList = new LinkedList<>();
linkedList.addFirst("First");   // O(1)
linkedList.addLast("Last");     // O(1)
linkedList.get(5);              // O(n) - must traverse
```

#### 3. Vector (Legacy - Avoid)
- Synchronized version of ArrayList
- Use `Collections.synchronizedList()` instead

### Key Methods

```java
// Adding elements
list.add("element");
list.add(index, "element");
list.addAll(anotherList);

// Accessing elements
String item = list.get(0);
int index = list.indexOf("element");

// Modifying
list.set(0, "newElement");
list.remove(0);
list.remove("element");

// Checking
boolean exists = list.contains("element");
int size = list.size();
boolean empty = list.isEmpty();

// Iterating
for (String item : list) { }
list.forEach(item -> System.out.println(item));
```

---

## Set Interface

### Definition
A collection that **does not allow duplicate elements**. Models the mathematical set abstraction.

### Common Implementations

#### 1. HashSet
- **Backed by**: HashMap
- **Performance**: O(1) for add, remove, contains
- **Ordering**: No guaranteed order
- **Use when**: You need unique elements and don't care about order

```java
Set<String> hashSet = new HashSet<>();
hashSet.add("Apple");
hashSet.add("Apple");  // Ignored - duplicate
System.out.println(hashSet.size());  // 1
```

#### 2. LinkedHashSet
- **Backed by**: Hash table + linked list
- **Performance**: O(1) for operations
- **Ordering**: Maintains insertion order
- **Use when**: You need unique elements AND insertion order

```java
Set<String> linkedHashSet = new LinkedHashSet<>();
linkedHashSet.add("Banana");
linkedHashSet.add("Apple");
linkedHashSet.add("Cherry");
// Order: Banana, Apple, Cherry
```

#### 3. TreeSet
- **Backed by**: Red-Black tree (self-balancing BST)
- **Performance**: O(log n) for operations
- **Ordering**: Sorted order (natural or custom)
- **Use when**: You need unique, sorted elements

```java
Set<String> treeSet = new TreeSet<>();
treeSet.add("Banana");
treeSet.add("Apple");
treeSet.add("Cherry");
// Order: Apple, Banana, Cherry (sorted)
```

### Key Methods

```java
// Adding
set.add("element");
set.addAll(anotherSet);

// Removing
set.remove("element");
set.clear();

// Checking
boolean exists = set.contains("element");
int size = set.size();

// Set operations
set1.retainAll(set2);  // Intersection
set1.addAll(set2);     // Union
set1.removeAll(set2);  // Difference
```

---

## Map Interface

### Definition
An object that maps **keys to values**. Cannot contain duplicate keys; each key maps to at most one value.

### Common Implementations

#### 1. HashMap
- **Performance**: O(1) for get/put (average)
- **Ordering**: No guaranteed order
- **Null**: Allows one null key, multiple null values
- **Use when**: You need fast key-value lookup

```java
Map<String, Integer> hashMap = new HashMap<>();
hashMap.put("John", 25);
hashMap.put("Jane", 30);
Integer age = hashMap.get("John");  // 25
```

#### 2. LinkedHashMap
- **Performance**: O(1) for get/put
- **Ordering**: Maintains insertion order
- **Use when**: You need predictable iteration order

```java
Map<String, String> linkedHashMap = new LinkedHashMap<>();
linkedHashMap.put("3", "Three");
linkedHashMap.put("1", "One");
linkedHashMap.put("2", "Two");
// Iteration order: 3, 1, 2
```

#### 3. TreeMap
- **Performance**: O(log n) for get/put
- **Ordering**: Sorted by keys
- **Null**: Does not allow null keys
- **Use when**: You need sorted keys

```java
Map<String, String> treeMap = new TreeMap<>();
treeMap.put("C", "Three");
treeMap.put("A", "One");
treeMap.put("B", "Two");
// Iteration order: A, B, C (sorted)
```

#### 4. Hashtable (Legacy - Avoid)
- Synchronized version of HashMap
- Use `Collections.synchronizedMap()` instead

### Key Methods

```java
// Adding/Updating
map.put("key", "value");
map.putAll(anotherMap);
map.putIfAbsent("key", "value");

// Accessing
String value = map.get("key");
String value = map.getOrDefault("key", "default");

// Removing
map.remove("key");
map.clear();

// Checking
boolean exists = map.containsKey("key");
boolean exists = map.containsValue("value");
int size = map.size();

// Iterating
for (Map.Entry<String, String> entry : map.entrySet()) {
    String key = entry.getKey();
    String value = entry.getValue();
}

map.forEach((key, value) -> {
    System.out.println(key + ": " + value);
});
```

---

## Choosing the Right Collection

### Decision Tree

```
Need key-value pairs?
  Yes → Use Map
    Need sorted keys? → TreeMap
    Need insertion order? → LinkedHashMap
    Just need fast lookup? → HashMap
  
  No → Need unique elements?
    Yes → Use Set
      Need sorted elements? → TreeSet
      Need insertion order? → LinkedHashSet
      Just need uniqueness? → HashSet
    
    No → Use List
      Frequent access by index? → ArrayList
      Frequent insertion/deletion? → LinkedList
```

### Performance Comparison

| Operation | ArrayList | LinkedList | HashSet | TreeSet | HashMap | TreeMap |
|-----------|-----------|------------|---------|---------|---------|---------|
| Add | O(1)* | O(1) | O(1) | O(log n) | O(1) | O(log n) |
| Remove | O(n) | O(1)** | O(1) | O(log n) | O(1) | O(log n) |
| Get | O(1) | O(n) | O(1) | O(log n) | O(1) | O(log n) |
| Contains | O(n) | O(n) | O(1) | O(log n) | O(1) | O(log n) |
| Iteration | Fast | Fast | Fast | Fast | Fast | Fast |

*Amortized O(1), occasionally O(n) for resizing
**O(1) if you have iterator position

---

## Real-World Scenarios

### Scenario 1: User Management System

**Problem**: Store user data where you need to quickly look up user by ID
**Solution**: HashMap

```java
Map<Long, User> userCache = new HashMap<>();
userCache.put(1L, new User("John"));
userCache.put(2L, new User("Jane"));

// Fast lookup
User user = userCache.get(1L);
```

### Scenario 2: Removing Duplicates from Data

**Problem**: Process a list of emails and ensure uniqueness
**Solution**: HashSet

```java
List<String> emailsWithDuplicates = Arrays.asList(
    "john@example.com",
    "jane@example.com",
    "john@example.com"  // duplicate
);

Set<String> uniqueEmails = new HashSet<>(emailsWithDuplicates);
// Result: 2 unique emails
```

### Scenario 3: Maintaining Ordered Product Catalog

**Problem**: Display products in the order they were added
**Solution**: LinkedHashMap or ArrayList

```java
Map<String, Product> catalog = new LinkedHashMap<>();
catalog.put("P001", new Product("Laptop"));
catalog.put("P002", new Product("Mouse"));
catalog.put("P003", new Product("Keyboard"));
// Maintains insertion order for display
```

### Scenario 4: Leaderboard System

**Problem**: Show users ranked by score
**Solution**: TreeMap with custom comparator

```java
Map<User, Integer> leaderboard = new TreeMap<>(
    (u1, u2) -> u2.getScore() - u1.getScore()  // Descending order
);
leaderboard.put(user1, 100);
leaderboard.put(user2, 200);
// Automatically sorted by score
```

---

## Demo Projects

Explore these hands-on demos to practice:

1. **[demo-arraylist-basics](demo-arraylist-basics/)** - ArrayList operations and use cases
2. **[demo-hashmap-cache](demo-hashmap-cache/)** - Implementing a simple cache
3. **[demo-set-operations](demo-set-operations/)** - Set operations for data processing
4. **[demo-collection-comparison](demo-collection-comparison/)** - Performance benchmarks

---

## Common Interview Questions

### Q1: Difference between ArrayList and LinkedList?
**A:** ArrayList uses dynamic array (fast random access O(1), slow insertion O(n)); LinkedList uses doubly-linked list (slow access O(n), fast insertion O(1)).

### Q2: How does HashMap work internally?
**A:** Uses array of buckets. hashCode() determines bucket, equals() resolves collisions. Java 8+ uses linked list → red-black tree for buckets with many collisions.

### Q3: When to use TreeSet vs HashSet?
**A:** Use HashSet for fast operations without ordering. Use TreeSet when you need elements in sorted order.

### Q4: Can HashMap have null keys?
**A:** Yes, HashMap allows one null key and multiple null values. TreeMap doesn't allow null keys.

---

## Best Practices

✅ **DO:**
- Use generics: `List<String>` not `List`
- Program to interfaces: `List<String>` not `ArrayList<String>`
- Initialize with capacity if size known: `new ArrayList<>(1000)`
- Use `Collections.unmodifiableList()` for immutable collections

❌ **DON'T:**
- Use Vector or Hashtable (use ArrayList and HashMap instead)
- Modify collection while iterating (use Iterator.remove())
- Compare collections with `==` (use `.equals()`)

---

## Next Steps

Continue to:
- **[Streams and Lambdas →](../02-streams-and-lambdas/)** - Process collections functionally

---

_Master collections, master Spring Boot! These are the building blocks of every application._
