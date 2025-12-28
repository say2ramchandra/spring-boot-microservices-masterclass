package com.masterclass.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Comprehensive demonstration of ArrayList operations.
 * 
 * ArrayList is the most commonly used List implementation.
 * It's backed by a dynamic array that automatically resizes.
 * 
 * Key Characteristics:
 * - Allows duplicate elements
 * - Maintains insertion order
 * - Fast random access by index: O(1)
 * - Slow insertion/deletion in middle: O(n)
 * - Best for: read-heavy operations, access by index
 * 
 * @author Spring Boot Microservices Masterclass
 */
public class ArrayListDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("        ArrayList Basics Demonstration");
        System.out.println("=".repeat(60));
        System.out.println();

        // Example 1: Creating ArrayList
        creatingArrayList();

        System.out.println("\n" + "=".repeat(60) + "\n");

        // Example 2: Adding Elements
        addingElements();

        System.out.println("\n" + "=".repeat(60) + "\n");

        // Example 3: Accessing Elements
        accessingElements();

        System.out.println("\n" + "=".repeat(60) + "\n");

        // Example 4: Modifying Elements
        modifyingElements();

        System.out.println("\n" + "=".repeat(60) + "\n");

        // Example 5: Removing Elements
        removingElements();

        System.out.println("\n" + "=".repeat(60) + "\n");

        // Example 6: Searching Elements
        searchingElements();

        System.out.println("\n" + "=".repeat(60) + "\n");

        // Example 7: Sorting
        sortingArrayList();

        System.out.println("\n" + "=".repeat(60) + "\n");

        // Example 8: Iterating
        iteratingArrayList();

        System.out.println("\n" + "=".repeat(60) + "\n");

        // Example 9: Real-World Example - Task Manager
        realWorldExample();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("           Demo Completed Successfully!");
        System.out.println("=".repeat(60));
    }

    /**
     * Example 1: Different ways to create ArrayList
     */
    private static void creatingArrayList() {
        System.out.println("📚 Example 1: Creating ArrayList");
        System.out.println("-".repeat(40));

        // Method 1: Default constructor (initial capacity 10)
        List<String> list1 = new ArrayList<>();
        System.out.println("1. Default constructor: " + list1);

        // Method 2: With initial capacity (performance optimization)
        List<String> list2 = new ArrayList<>(100);
        System.out.println("2. With capacity 100: " + list2);

        // Method 3: From another collection
        List<String> list3 = new ArrayList<>(List.of("Apple", "Banana", "Cherry"));
        System.out.println("3. From collection: " + list3);

        // Method 4: Using List.of() - Immutable (Java 9+)
        List<String> immutableList = List.of("A", "B", "C");
        System.out.println("4. Immutable list: " + immutableList);
        // immutableList.add("D"); // This would throw UnsupportedOperationException

        System.out.println("\n💡 Tip: Use initial capacity when size is known for better performance!");
    }

    /**
     * Example 2: Adding elements to ArrayList
     */
    private static void addingElements() {
        System.out.println("➕ Example 2: Adding Elements");
        System.out.println("-".repeat(40));

        List<String> fruits = new ArrayList<>();

        // Add at end
        fruits.add("Apple");
        fruits.add("Banana");
        fruits.add("Cherry");
        System.out.println("After adding 3 fruits: " + fruits);

        // Add at specific index
        fruits.add(1, "Avocado");  // Shifts Banana to index 2
        System.out.println("After adding at index 1: " + fruits);

        // Add multiple elements
        List<String> moreFruits = List.of("Date", "Elderberry");
        fruits.addAll(moreFruits);
        System.out.println("After adding collection: " + fruits);

        // Add at specific position
        fruits.addAll(0, List.of("Apricot", "Acai"));
        System.out.println("After adding at start: " + fruits);

        System.out.println("\n💡 Adding at end is O(1), adding in middle is O(n)!");
    }

    /**
     * Example 3: Accessing elements
     */
    private static void accessingElements() {
        System.out.println("🔍 Example 3: Accessing Elements");
        System.out.println("-".repeat(40));

        List<String> languages = new ArrayList<>(
            List.of("Java", "Python", "JavaScript", "C++", "Go")
        );

        // Get by index (most common operation)
        String first = languages.get(0);
        String last = languages.get(languages.size() - 1);
        System.out.println("First language: " + first);
        System.out.println("Last language: " + last);

        // Get first and last (alternative)
        System.out.println("First: " + languages.getFirst());  // Java 21+
        System.out.println("Last: " + languages.getLast());    // Java 21+

        // Check if list contains element
        boolean hasJava = languages.contains("Java");
        System.out.println("Contains 'Java': " + hasJava);

        // Check if empty
        boolean empty = languages.isEmpty();
        System.out.println("Is empty: " + empty);

        // Get size
        int size = languages.size();
        System.out.println("Size: " + size);

        System.out.println("\n💡 get() is O(1) - super fast for ArrayList!");
    }

    /**
     * Example 4: Modifying elements
     */
    private static void modifyingElements() {
        System.out.println("✏️ Example 4: Modifying Elements");
        System.out.println("-".repeat(40));

        List<String> cities = new ArrayList<>(
            List.of("New York", "London", "Tokyo", "Paris")
        );
        System.out.println("Original: " + cities);

        // Replace element at index
        String oldValue = cities.set(2, "Kyoto");
        System.out.println("Replaced '" + oldValue + "' with 'Kyoto': " + cities);

        // Replace using replaceAll (Java 8+)
        cities.replaceAll(city -> city.toUpperCase());
        System.out.println("All uppercase: " + cities);

        System.out.println("\n💡 set() is O(1) - very efficient!");
    }

    /**
     * Example 5: Removing elements
     */
    private static void removingElements() {
        System.out.println("❌ Example 5: Removing Elements");
        System.out.println("-".repeat(40));

        List<String> numbers = new ArrayList<>(
            List.of("One", "Two", "Three", "Four", "Five", "Six")
        );
        System.out.println("Original: " + numbers);

        // Remove by index
        String removed = numbers.remove(0);
        System.out.println("Removed by index 0: '" + removed + "' → " + numbers);

        // Remove by object
        boolean success = numbers.remove("Four");
        System.out.println("Removed 'Four': " + success + " → " + numbers);

        // Remove all occurrences
        List<String> toRemove = List.of("Two", "Six");
        numbers.removeAll(toRemove);
        System.out.println("Removed collection: " + numbers);

        // Remove if condition matches (Java 8+)
        List<Integer> nums = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        nums.removeIf(n -> n % 2 == 0);  // Remove even numbers
        System.out.println("Removed even numbers: " + nums);

        // Clear all elements
        numbers.clear();
        System.out.println("After clear: " + numbers);

        System.out.println("\n💡 Removing from middle is O(n) due to shifting!");
    }

    /**
     * Example 6: Searching elements
     */
    private static void searchingElements() {
        System.out.println("🔎 Example 6: Searching Elements");
        System.out.println("-".repeat(40));

        List<String> names = new ArrayList<>(
            List.of("Alice", "Bob", "Charlie", "David", "Eve", "Bob")
        );

        // Find first occurrence
        int firstBob = names.indexOf("Bob");
        System.out.println("First 'Bob' at index: " + firstBob);

        // Find last occurrence
        int lastBob = names.lastIndexOf("Bob");
        System.out.println("Last 'Bob' at index: " + lastBob);

        // Element not found
        int notFound = names.indexOf("Zara");
        System.out.println("'Zara' index: " + notFound + " (returns -1 if not found)");

        // Check if contains
        System.out.println("Contains 'Eve': " + names.contains("Eve"));

        // Check if contains any/all elements
        List<String> searchList = List.of("Alice", "Bob");
        System.out.println("Contains all [Alice, Bob]: " + names.containsAll(searchList));

        System.out.println("\n💡 indexOf() is O(n) - must search through list!");
    }

    /**
     * Example 7: Sorting ArrayList
     */
    private static void sortingArrayList() {
        System.out.println("📊 Example 7: Sorting ArrayList");
        System.out.println("-".repeat(40));

        // Sort strings
        List<String> fruits = new ArrayList<>(
            List.of("Banana", "Apple", "Cherry", "Date", "Elderberry")
        );
        System.out.println("Original: " + fruits);

        Collections.sort(fruits);
        System.out.println("Sorted (natural): " + fruits);

        fruits.sort(Collections.reverseOrder());
        System.out.println("Sorted (reverse): " + fruits);

        // Sort numbers
        List<Integer> numbers = new ArrayList<>(List.of(5, 2, 8, 1, 9, 3));
        System.out.println("\nOriginal numbers: " + numbers);

        Collections.sort(numbers);
        System.out.println("Sorted ascending: " + numbers);

        numbers.sort((a, b) -> b - a);  // Lambda comparator
        System.out.println("Sorted descending: " + numbers);

        // Sort custom objects
        List<Person> people = new ArrayList<>(List.of(
            new Person("Alice", 30),
            new Person("Bob", 25),
            new Person("Charlie", 35)
        ));

        people.sort((p1, p2) -> p1.age - p2.age);
        System.out.println("\nPeople sorted by age: " + people);

        System.out.println("\n💡 Use Collections.sort() or list.sort() with comparators!");
    }

    /**
     * Example 8: Different ways to iterate
     */
    private static void iteratingArrayList() {
        System.out.println("🔄 Example 8: Iterating ArrayList");
        System.out.println("-".repeat(40));

        List<String> colors = new ArrayList<>(
            List.of("Red", "Green", "Blue", "Yellow")
        );

        // Method 1: Enhanced for loop (most common)
        System.out.println("1. Enhanced for loop:");
        for (String color : colors) {
            System.out.print("   " + color + " ");
        }
        System.out.println();

        // Method 2: Traditional for loop (when index needed)
        System.out.println("2. Traditional for loop:");
        for (int i = 0; i < colors.size(); i++) {
            System.out.print("   [" + i + "]=" + colors.get(i) + " ");
        }
        System.out.println();

        // Method 3: forEach with lambda (Java 8+)
        System.out.println("3. forEach with lambda:");
        colors.forEach(color -> System.out.print("   " + color + " "));
        System.out.println();

        // Method 4: Iterator (safe for removal during iteration)
        System.out.println("4. Iterator:");
        var iterator = colors.iterator();
        while (iterator.hasNext()) {
            System.out.print("   " + iterator.next() + " ");
        }
        System.out.println();

        // Method 5: Stream API (Java 8+)
        System.out.println("5. Stream API:");
        colors.stream()
              .map(String::toUpperCase)
              .forEach(color -> System.out.print("   " + color + " "));
        System.out.println();

        System.out.println("\n💡 Choose iteration method based on use case!");
    }

    /**
     * Example 9: Real-world example - Simple Task Manager
     */
    private static void realWorldExample() {
        System.out.println("🌍 Example 9: Real-World - Task Manager");
        System.out.println("-".repeat(40));

        TaskManager taskManager = new TaskManager();

        // Add tasks
        taskManager.addTask("Complete project proposal");
        taskManager.addTask("Review pull requests");
        taskManager.addTask("Update documentation");
        taskManager.addTask("Fix bug #123");

        // Display all tasks
        taskManager.displayTasks();

        // Mark task as complete
        taskManager.completeTask(1);

        // Display remaining tasks
        System.out.println("\nAfter completing task 1:");
        taskManager.displayTasks();

        // Get pending count
        System.out.println("\nTotal pending tasks: " + taskManager.getPendingTaskCount());

        System.out.println("\n💡 ArrayList perfect for task lists - ordered and dynamic!");
    }

    // Helper class for sorting example
    static class Person {
        String name;
        int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return name + "(" + age + ")";
        }
    }

    // Task Manager class for real-world example
    static class TaskManager {
        private List<String> tasks;

        public TaskManager() {
            this.tasks = new ArrayList<>();
        }

        public void addTask(String task) {
            tasks.add(task);
            System.out.println("✓ Added: " + task);
        }

        public void completeTask(int index) {
            if (index >= 0 && index < tasks.size()) {
                String completed = tasks.remove(index);
                System.out.println("✓ Completed: " + completed);
            } else {
                System.out.println("✗ Invalid task index!");
            }
        }

        public void displayTasks() {
            if (tasks.isEmpty()) {
                System.out.println("No pending tasks!");
                return;
            }

            System.out.println("\nPending Tasks:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + tasks.get(i));
            }
        }

        public int getPendingTaskCount() {
            return tasks.size();
        }
    }
}
