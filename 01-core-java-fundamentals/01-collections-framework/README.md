# Collections Framework

> **Master Java Collections - The foundation of data manipulation in Spring Boot**

## 📚 Table of Contents

1. [Introduction](#introduction)
2. [Collection Hierarchy](#collection-hierarchy)
3. [List Interface](#list-interface)
4. [Set Interface](#set-interface)
5. [Queue Interface](#queue-interface)
6. [Map Interface](#map-interface)
7. [Internal Implementations](#internal-implementations)
8. [Choosing the Right Collection](#choosing-the-right-collection)
9. [Real-World Scenarios](#real-world-scenarios)

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

#### TreeSet NavigableSet Methods

```java
TreeSet<Integer> numbers = new TreeSet<>(List.of(10, 20, 30, 40, 50));

// Navigation methods
numbers.first();        // 10 (smallest)
numbers.last();         // 50 (largest)
numbers.lower(30);      // 20 (strictly less than 30)
numbers.floor(30);      // 30 (less than or equal to 30)
numbers.higher(30);     // 40 (strictly greater than 30)
numbers.ceiling(30);    // 30 (greater than or equal to 30)

// Subset views
numbers.headSet(30);           // [10, 20] (elements < 30)
numbers.tailSet(30);           // [30, 40, 50] (elements >= 30)
numbers.subSet(20, 40);        // [20, 30] (from 20 inclusive to 40 exclusive)
numbers.subSet(20, true, 40, true);  // [20, 30, 40] (both inclusive)

// Polling (remove and return)
numbers.pollFirst();    // Removes and returns 10
numbers.pollLast();     // Removes and returns 50

// Descending order
NavigableSet<Integer> descending = numbers.descendingSet();
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

## Queue Interface

### Definition
A collection designed for holding elements prior to processing. Typically **FIFO** (First-In-First-Out), but can vary (e.g., PriorityQueue).

### Common Implementations

#### 1. LinkedList (as Queue)
- **Backed by**: Doubly-linked list
- **Performance**: O(1) for queue operations
- **Use when**: Simple FIFO queue

```java
Queue<String> queue = new LinkedList<>();
queue.offer("First");    // Add to tail
queue.offer("Second");
queue.peek();            // View head without removing
queue.poll();            // Remove from head
```

#### 2. ArrayDeque
- **Backed by**: Resizable circular array
- **Performance**: O(1) for operations (faster than LinkedList)
- **Use when**: Stack or Queue operations (preferred over Stack class)

```java
Deque<String> deque = new ArrayDeque<>();
// As Queue (FIFO)
deque.offerLast("element");
deque.pollFirst();

// As Stack (LIFO)
deque.push("element");   // addFirst
deque.pop();             // removeFirst
```

#### 3. PriorityQueue
- **Backed by**: Binary heap (array-based)
- **Performance**: O(log n) for add/remove, O(1) for peek
- **Ordering**: Natural order or custom Comparator
- **Use when**: Need elements processed by priority

```java
// Min-heap (smallest first - default)
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
minHeap.offer(30);
minHeap.offer(10);
minHeap.offer(20);
minHeap.poll();  // Returns 10 (smallest)

// Max-heap (largest first)
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());

// Custom priority
PriorityQueue<Task> taskQueue = new PriorityQueue<>(
    Comparator.comparingInt(Task::getPriority).reversed()
);
```

### Queue Methods

| Method | Throws Exception | Returns Special Value |
|--------|------------------|----------------------|
| Insert | `add(e)` | `offer(e)` → false if full |
| Remove | `remove()` | `poll()` → null if empty |
| Examine | `element()` | `peek()` → null if empty |

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

#### TreeMap NavigableMap Methods

```java
TreeMap<Integer, String> map = new TreeMap<>();
map.put(10, "Ten");
map.put(20, "Twenty");
map.put(30, "Thirty");
map.put(40, "Forty");

// Navigation methods
map.firstKey();         // 10
map.lastKey();          // 40
map.firstEntry();       // 10=Ten
map.lastEntry();        // 40=Forty

map.lowerKey(25);       // 20 (strictly less)
map.floorKey(30);       // 30 (less than or equal)
map.higherKey(25);      // 30 (strictly greater)
map.ceilingKey(25);     // 30 (greater than or equal)

// Submap views
map.headMap(30);              // {10=Ten, 20=Twenty} (keys < 30)
map.tailMap(30);              // {30=Thirty, 40=Forty} (keys >= 30)
map.subMap(15, 35);           // {20=Twenty, 30=Thirty}

// Polling
map.pollFirstEntry();   // Removes and returns 10=Ten
map.pollLastEntry();    // Removes and returns 40=Forty

// Descending order
NavigableMap<Integer, String> descending = map.descendingMap();
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

## Internal Implementations

Understanding how collections work internally helps you make better choices and write more efficient code.

### ArrayList Internal Implementation

```
┌─────────────────────────────────────────────────────────┐
│  ArrayList = Object[] elementData + int size            │
├─────────────────────────────────────────────────────────┤
│  Index:  [0]    [1]    [2]    [3]    [4]    [5]  ...   │
│  Data:   "A"    "B"    "C"   null   null   null  ...   │
│                        ↑                                │
│                      size=3                             │
│                      capacity=10 (default)              │
└─────────────────────────────────────────────────────────┘
```

**Key Implementation Details:**
- **Backing array**: `Object[] elementData`
- **Default capacity**: 10 (on first add)
- **Growth formula**: `newCapacity = oldCapacity + (oldCapacity >> 1)` (1.5x growth)
- **Resizing**: Creates new array, copies elements via `Arrays.copyOf()`

```java
// What happens during add() when array is full:
// 1. Calculate new capacity (1.5x old)
// 2. Create new array with new capacity
// 3. Copy all elements to new array
// 4. Point elementData to new array
// 5. Add new element
// This is why add() is "amortized O(1)" - usually O(1), occasionally O(n)
```

---

### LinkedList Internal Implementation

```
┌─────────────────────────────────────────────────────────────────┐
│  LinkedList = Doubly Linked List                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────┐    ┌──────┐    ┌──────┐    ┌──────┐                  │
│  │ Node │ ←→ │ Node │ ←→ │ Node │ ←→ │ Node │                  │
│  │ "A"  │    │ "B"  │    │ "C"  │    │ "D"  │                  │
│  └──────┘    └──────┘    └──────┘    └──────┘                  │
│     ↑                                    ↑                      │
│   first                                last                     │
│                                                                 │
│  Node structure:                                                │
│  ┌─────────────────────┐                                       │
│  │ E item              │  (the data)                           │
│  │ Node<E> prev        │  (pointer to previous)                │
│  │ Node<E> next        │  (pointer to next)                    │
│  └─────────────────────┘                                       │
└─────────────────────────────────────────────────────────────────┘
```

**Key Implementation Details:**
- **No backing array** - only Node objects
- **Each node**: contains data + prev pointer + next pointer
- **Access by index**: Must traverse from first or last (whichever is closer)
- **Memory overhead**: ~24 extra bytes per element (for Node object + pointers)

---

### HashMap Internal Implementation

```
┌────────────────────────────────────────────────────────────────────┐
│  HashMap = Node<K,V>[] table (array of buckets)                    │
├────────────────────────────────────────────────────────────────────┤
│                                                                    │
│  Bucket Index = hash(key) & (table.length - 1)                     │
│                                                                    │
│  Index: [0]     [1]     [2]     [3]     [4]    ...                │
│          │       │       │       │       │                        │
│          ↓       ↓       ↓       ↓       ↓                        │
│        null   ┌─────┐  null   ┌─────┐  null                       │
│               │K1,V1│         │K3,V3│                             │
│               └──┬──┘         └──┬──┘                             │
│                  ↓               ↓                                 │
│               ┌─────┐         ┌─────┐   (collision → linked list) │
│               │K2,V2│         │K4,V4│                             │
│               └─────┘         └──┬──┘                             │
│                                  ↓                                 │
│                               (if > 8 nodes, converts to Tree)    │
│                                                                    │
│  Node<K,V> structure:                                             │
│  ┌───────────────────┐                                            │
│  │ int hash          │  (cached hashCode)                         │
│  │ K key             │                                            │
│  │ V value           │                                            │
│  │ Node<K,V> next    │  (for chaining)                            │
│  └───────────────────┘                                            │
└────────────────────────────────────────────────────────────────────┘
```

**Key Implementation Details:**
- **Default capacity**: 16 buckets
- **Load factor**: 0.75 (resize when 75% full)
- **Growth**: Doubles capacity (16 → 32 → 64...)
- **Hash function**: `hash = key.hashCode() ^ (hashCode >>> 16)` (spreads bits)
- **Bucket index**: `index = hash & (length - 1)` (fast modulo for power of 2)
- **Java 8+**: Bucket converts from LinkedList to Red-Black Tree when > 8 entries (O(n) → O(log n))
- **Treeify threshold**: 8 (convert to tree), Untreeify threshold: 6 (back to list)

```java
// Put operation flow:
// 1. Calculate hash: hash(key)
// 2. Find bucket: hash & (table.length - 1)
// 3. If bucket empty: create new Node
// 4. If bucket has nodes:
//    a. Check if key exists (using equals())
//    b. If exists: update value
//    c. If not: add to chain (or tree)
// 5. If size > threshold: resize
```

---

### HashSet Internal Implementation

```
┌────────────────────────────────────────────────────────────┐
│  HashSet is backed by HashMap!                             │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  private transient HashMap<E, Object> map;                 │
│  private static final Object PRESENT = new Object();       │
│                                                            │
│  public boolean add(E e) {                                 │
│      return map.put(e, PRESENT) == null;                   │
│  }                                                         │
│                                                            │
│  // All elements stored as KEYS in the HashMap             │
│  // PRESENT is just a dummy value                          │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

**Key Points:**
- HashSet wraps HashMap
- Elements are stored as **keys** (not values)
- All values are the same dummy object `PRESENT`
- Inherits all HashMap characteristics (hashing, load factor, etc.)

---

### LinkedHashMap / LinkedHashSet Internal Implementation

```
┌────────────────────────────────────────────────────────────────────┐
│  LinkedHashMap = HashMap + Doubly Linked List                      │
├────────────────────────────────────────────────────────────────────┤
│                                                                    │
│  HashMap buckets (for O(1) access):                                │
│  [0]  [1]  [2]  [3]  [4]  [5]  ...                                │
│        │         │                                                 │
│        ↓         ↓                                                 │
│      Entry1   Entry3                                               │
│        │                                                           │
│        ↓                                                           │
│      Entry2                                                        │
│                                                                    │
│  Plus: Doubly linked list connecting ALL entries:                  │
│                                                                    │
│  head → Entry1 ←→ Entry2 ←→ Entry3 → tail                         │
│         (first)            (last)                                  │
│         inserted           inserted                                │
│                                                                    │
│  Entry extends HashMap.Node:                                       │
│  ┌─────────────────────┐                                          │
│  │ (HashMap.Node)      │                                          │
│  │ Entry<K,V> before   │  (previous in insertion order)           │
│  │ Entry<K,V> after    │  (next in insertion order)               │
│  └─────────────────────┘                                          │
└────────────────────────────────────────────────────────────────────┘
```

**Key Points:**
- Maintains two structures: hash table (for speed) + linked list (for order)
- Each entry has `before` and `after` pointers
- Access-order mode available: `new LinkedHashMap<>(16, 0.75f, true)`
- Useful for LRU cache implementation (override `removeEldestEntry()`)

---

### TreeMap / TreeSet Internal Implementation

```
┌────────────────────────────────────────────────────────────────────┐
│  TreeMap = Red-Black Tree (Self-Balancing BST)                     │
├────────────────────────────────────────────────────────────────────┤
│                                                                    │
│  Red-Black Tree Properties:                                        │
│  1. Every node is RED or BLACK                                     │
│  2. Root is always BLACK                                           │
│  3. Red nodes cannot have red children                             │
│  4. Every path from root to null has same number of black nodes    │
│                                                                    │
│              ┌─────────┐                                           │
│              │20 (BLK) │  ← root                                   │
│              └────┬────┘                                           │
│           ┌───────┴───────┐                                        │
│     ┌─────┴─────┐   ┌─────┴─────┐                                  │
│     │ 10 (RED)  │   │ 30 (RED)  │                                  │
│     └─────┬─────┘   └─────┬─────┘                                  │
│      ┌────┴────┐     ┌────┴────┐                                   │
│  ┌───┴───┐ ┌───┴───┐ (null)  ┌───┴───┐                            │
│  │5 (BLK)│ │15(BLK)│         │40(BLK)│                             │
│  └───────┘ └───────┘         └───────┘                             │
│                                                                    │
│  Entry structure:                                                  │
│  ┌─────────────────────┐                                          │
│  │ K key               │                                          │
│  │ V value             │                                          │
│  │ Entry<K,V> left     │                                          │
│  │ Entry<K,V> right    │                                          │
│  │ Entry<K,V> parent   │                                          │
│  │ boolean color       │  (RED=false, BLACK=true)                 │
│  └─────────────────────┘                                          │
└────────────────────────────────────────────────────────────────────┘
```

**Key Implementation Details:**
- **Self-balancing**: Guarantees O(log n) for all operations
- **Rotations**: Left-rotate and right-rotate to maintain balance
- **Recoloring**: Change node colors to satisfy red-black properties
- **Comparator**: Uses natural ordering (Comparable) or custom Comparator
- **No null keys**: Cannot compare null (throws NullPointerException)

**Why Red-Black Tree (not AVL)?**
- Less strict balancing = fewer rotations on insert/delete
- Better for write-heavy workloads
- AVL has faster lookups but more rebalancing overhead

---

### PriorityQueue Internal Implementation

```
┌────────────────────────────────────────────────────────────────────┐
│  PriorityQueue = Binary Heap (array-based)                         │
├────────────────────────────────────────────────────────────────────┤
│                                                                    │
│  Min-Heap property: parent <= children                             │
│                                                                    │
│  Tree view:           Array view:                                  │
│       ┌───┐          ┌───┬───┬───┬───┬───┬───┬───┐               │
│       │ 1 │          │ 1 │ 3 │ 2 │ 7 │ 4 │ 5 │ 6 │               │
│       └─┬─┘          └───┴───┴───┴───┴───┴───┴───┘               │
│     ┌───┴───┐          [0] [1] [2] [3] [4] [5] [6]               │
│   ┌─┴─┐   ┌─┴─┐                                                   │
│   │ 3 │   │ 2 │        Parent of i: (i-1)/2                       │
│   └─┬─┘   └─┬─┘        Left child:  2*i + 1                       │
│  ┌──┴──┐ ┌──┴──┐       Right child: 2*i + 2                       │
│ ┌┴┐  ┌─┴┐ ┌┴┐ ┌┴┐                                                 │
│ │7│  │4 │ │5│ │6│                                                 │
│ └─┘  └──┘ └─┘ └─┘                                                 │
│                                                                    │
└────────────────────────────────────────────────────────────────────┘
```

**Key Implementation Details:**
- **Binary heap**: Complete binary tree stored in array
- **No actual tree nodes**: Children calculated via index math
- **Heap operations**:
  - **offer()**: Add to end, then "sift up" (bubble up)
  - **poll()**: Remove root, move last to root, "sift down" (bubble down)
- **Not sorted**: Only guarantees smallest at root!
- **Default capacity**: 11, grows by 50% if small or 25% if large

---

### ArrayDeque Internal Implementation

```
┌────────────────────────────────────────────────────────────────────┐
│  ArrayDeque = Circular Array                                       │
├────────────────────────────────────────────────────────────────────┤
│                                                                    │
│  ┌───┬───┬───┬───┬───┬───┬───┬───┐                                │
│  │   │ C │ D │ E │   │   │ A │ B │                                │
│  └───┴───┴───┴───┴───┴───┴───┴───┘                                │
│    0   1   2   3   4   5   6   7                                  │
│                            ↑   ↑                                   │
│                          head  │                                   │
│                    (first element)                                 │
│                            tail (next insert position)             │
│                        ↓                                           │
│    ... E │   │   │ A │ B │ C │ D │ E ...                          │
│          4       6   7   0   1   2                                 │
│                  ↑               ↑                                  │
│                head            tail                                │
│                                                                    │
│  Circular: tail can wrap around to beginning                       │
│  head = (head - 1) & (length - 1)  // decrement, wrap              │
│  tail = (tail + 1) & (length - 1)  // increment, wrap              │
└────────────────────────────────────────────────────────────────────┘
```

**Key Implementation Details:**
- **Circular buffer**: No shifting elements on add/remove
- **Power of 2 size**: Enables fast modulo with bitwise AND
- **Resize**: Doubles when full, copies in order
- **Faster than LinkedList**: Better cache locality, no node allocation

---

### Summary: When to Use What

| Collection | Internal Structure | Best For |
|------------|-------------------|----------|
| ArrayList | Dynamic array | Random access, iteration |
| LinkedList | Doubly-linked list | Frequent insert/remove at ends |
| HashMap | Hash table + linked list/tree | Key-value lookup |
| LinkedHashMap | HashMap + linked list | Ordered key-value |
| TreeMap | Red-Black tree | Sorted keys, range queries |
| HashSet | HashMap wrapper | Unique elements |
| LinkedHashSet | LinkedHashMap wrapper | Ordered unique elements |
| TreeSet | TreeMap wrapper | Sorted unique elements |
| PriorityQueue | Binary heap | Priority-based processing |
| ArrayDeque | Circular array | Stack/Queue operations |

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
2. **[demo-hashmap-cache](demo-hashmap-cache/)** - Implementing a simple cache with HashMap
3. **[demo-queue-priority](demo-queue-priority/)** - Queue, Deque, PriorityQueue, and immutable collections

---

## Test Your Knowledge

### Q1: Difference between ArrayList and LinkedList?
**A:** ArrayList uses dynamic array (fast random access O(1), slow insertion O(n)); LinkedList uses doubly-linked list (slow access O(n), fast insertion O(1)).

### Q2: How does HashMap work internally?
**A:** Uses array of buckets. `hashCode()` determines bucket index, `equals()` resolves collisions. Java 8+ converts linked list → red-black tree when bucket has >8 entries.

### Q3: When to use TreeSet vs HashSet?
**A:** Use HashSet for O(1) operations when order doesn't matter. Use TreeSet when you need sorted elements or range queries (subSet, headSet, tailSet).

### Q4: Can HashMap have null keys?
**A:** Yes, HashMap allows one null key and multiple null values. TreeMap doesn't allow null keys (needs comparison).

### Q5: What's the difference between TreeMap and LinkedHashMap?
**A:** TreeMap sorts by keys (natural order or Comparator), O(log n). LinkedHashMap maintains insertion order (or access order), O(1). Use TreeMap for sorted iteration, LinkedHashMap for predictable iteration in insertion order.

### Q6: Why does PriorityQueue not sort the entire collection?
**A:** PriorityQueue uses a binary heap which only guarantees the smallest (or largest with Comparator) element is at the head. Full sorting would be O(n log n) on every insert, while heap operations are O(log n).

### Q7: What happens when HashMap reaches load factor?
**A:** HashMap doubles its bucket array size and rehashes all entries to new positions. This is expensive (O(n)), so specify initial capacity if you know the size.

### Q8: TreeSet vs TreeMap relationship?
**A:** TreeSet is backed by TreeMap (like HashSet/HashMap). Elements are stored as keys with a dummy value. Both use Red-Black tree internally.

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
