package com.masterclass.streams;

import java.util.*;
import java.util.stream.*;

/**
 * Comprehensive demonstration of Java Stream API operations.
 * 
 * This demo covers:
 * 1. Creating streams
 * 2. Intermediate operations (filter, map, flatMap, distinct, sorted, limit, skip)
 * 3. Terminal operations (collect, forEach, reduce, count, anyMatch, findFirst)
 * 4. Real-world examples with Employee data
 * 
 * @author Spring Boot Microservices Masterclass
 */
public class StreamOperationsDemo {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("JAVA STREAMS API - COMPREHENSIVE DEMO");
        System.out.println("=".repeat(80));
        
        // Part 1: Creating Streams
        demonstrateStreamCreation();
        
        // Part 2: Intermediate Operations
        demonstrateIntermediateOperations();
        
        // Part 3: Terminal Operations
        demonstrateTerminalOperations();
        
        // Part 4: Real-World Example - Employee Management
        demonstrateEmployeeManagement();
        
        // Part 5: Advanced Stream Operations
        demonstrateAdvancedOperations();
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Demo completed successfully!");
        System.out.println("=".repeat(80));
    }
    
    // ============================================================================
    // PART 1: CREATING STREAMS
    // ============================================================================
    
    private static void demonstrateStreamCreation() {
        System.out.println("\n" + "-".repeat(80));
        System.out.println("PART 1: CREATING STREAMS");
        System.out.println("-".repeat(80));
        
        // Method 1: From Collection
        System.out.println("\n1. From Collection:");
        List<String> fruits = Arrays.asList("Apple", "Banana", "Cherry");
        fruits.stream().forEach(fruit -> System.out.println("   - " + fruit));
        
        // Method 2: From Array
        System.out.println("\n2. From Array:");
        String[] colors = {"Red", "Green", "Blue"};
        Arrays.stream(colors).forEach(color -> System.out.println("   - " + color));
        
        // Method 3: Using Stream.of()
        System.out.println("\n3. Using Stream.of():");
        Stream.of("Java", "Python", "JavaScript")
            .forEach(lang -> System.out.println("   - " + lang));
        
        // Method 4: Infinite Stream with limit
        System.out.println("\n4. Infinite Stream (limited to 5):");
        Stream.iterate(1, n -> n + 1)
            .limit(5)
            .forEach(num -> System.out.println("   - Number: " + num));
        
        // Method 5: Stream.generate()
        System.out.println("\n5. Generated Stream (5 random numbers):");
        Stream.generate(Math::random)
            .limit(5)
            .forEach(num -> System.out.printf("   - %.4f%n", num));
        
        // Method 6: IntStream range
        System.out.println("\n6. IntStream range:");
        IntStream.range(1, 6)
            .forEach(num -> System.out.println("   - " + num));
    }
    
    // ============================================================================
    // PART 2: INTERMEDIATE OPERATIONS (Lazy - don't execute until terminal operation)
    // ============================================================================
    
    private static void demonstrateIntermediateOperations() {
        System.out.println("\n" + "-".repeat(80));
        System.out.println("PART 2: INTERMEDIATE OPERATIONS");
        System.out.println("-".repeat(80));
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 1. filter() - Select elements
        System.out.println("\n1. filter() - Get even numbers:");
        numbers.stream()
            .filter(n -> n % 2 == 0)
            .forEach(n -> System.out.print("   " + n + " "));
        System.out.println();
        
        // 2. map() - Transform elements
        System.out.println("\n2. map() - Square each number:");
        numbers.stream()
            .map(n -> n * n)
            .forEach(n -> System.out.print("   " + n + " "));
        System.out.println();
        
        // 3. flatMap() - Flatten nested structures
        System.out.println("\n3. flatMap() - Flatten nested lists:");
        List<List<Integer>> nestedList = Arrays.asList(
            Arrays.asList(1, 2, 3),
            Arrays.asList(4, 5, 6),
            Arrays.asList(7, 8, 9)
        );
        nestedList.stream()
            .flatMap(Collection::stream)
            .forEach(n -> System.out.print("   " + n + " "));
        System.out.println();
        
        // 4. distinct() - Remove duplicates
        System.out.println("\n4. distinct() - Remove duplicates:");
        List<Integer> duplicates = Arrays.asList(1, 2, 2, 3, 3, 3, 4, 5);
        duplicates.stream()
            .distinct()
            .forEach(n -> System.out.print("   " + n + " "));
        System.out.println();
        
        // 5. sorted() - Sort elements
        System.out.println("\n5. sorted() - Sort names:");
        List<String> names = Arrays.asList("Charlie", "Alice", "Bob", "David");
        names.stream()
            .sorted()
            .forEach(name -> System.out.println("   - " + name));
        
        // 6. sorted() with custom comparator
        System.out.println("\n6. sorted() with Comparator (reverse order):");
        numbers.stream()
            .sorted(Comparator.reverseOrder())
            .limit(5)
            .forEach(n -> System.out.print("   " + n + " "));
        System.out.println();
        
        // 7. limit() - Limit stream size
        System.out.println("\n7. limit() - First 3 numbers:");
        numbers.stream()
            .limit(3)
            .forEach(n -> System.out.print("   " + n + " "));
        System.out.println();
        
        // 8. skip() - Skip first N elements
        System.out.println("\n8. skip() - Skip first 5 numbers:");
        numbers.stream()
            .skip(5)
            .forEach(n -> System.out.print("   " + n + " "));
        System.out.println();
        
        // 9. peek() - Debug intermediate steps
        System.out.println("\n9. peek() - Debug stream pipeline:");
        numbers.stream()
            .filter(n -> n % 2 == 0)
            .peek(n -> System.out.println("   Filtered: " + n))
            .map(n -> n * 2)
            .peek(n -> System.out.println("   Mapped: " + n))
            .limit(3)
            .collect(Collectors.toList());
    }
    
    // ============================================================================
    // PART 3: TERMINAL OPERATIONS (Eager - trigger stream execution)
    // ============================================================================
    
    private static void demonstrateTerminalOperations() {
        System.out.println("\n" + "-".repeat(80));
        System.out.println("PART 3: TERMINAL OPERATIONS");
        System.out.println("-".repeat(80));
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 1. collect() - Collect to List
        System.out.println("\n1. collect() to List:");
        List<Integer> evenNumbers = numbers.stream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toList());
        System.out.println("   Even numbers: " + evenNumbers);
        
        // 2. collect() - Collect to Set
        System.out.println("\n2. collect() to Set:");
        Set<Integer> uniqueNumbers = Arrays.asList(1, 2, 2, 3, 3, 3).stream()
            .collect(Collectors.toSet());
        System.out.println("   Unique numbers: " + uniqueNumbers);
        
        // 3. collect() - Joining strings
        System.out.println("\n3. collect() with joining:");
        String joined = Arrays.asList("Java", "Spring", "Boot").stream()
            .collect(Collectors.joining(", "));
        System.out.println("   Technologies: " + joined);
        
        // 4. forEach() - Perform action
        System.out.println("\n4. forEach() - Print each element:");
        numbers.stream()
            .limit(5)
            .forEach(n -> System.out.print("   " + n + " "));
        System.out.println();
        
        // 5. reduce() - Reduce to single value
        System.out.println("\n5. reduce() - Sum of numbers:");
        int sum = numbers.stream()
            .reduce(0, Integer::sum);
        System.out.println("   Sum: " + sum);
        
        System.out.println("\n6. reduce() - Product of numbers:");
        int product = Arrays.asList(1, 2, 3, 4, 5).stream()
            .reduce(1, (a, b) -> a * b);
        System.out.println("   Product: " + product);
        
        // 7. count() - Count elements
        System.out.println("\n7. count() - Count even numbers:");
        long count = numbers.stream()
            .filter(n -> n % 2 == 0)
            .count();
        System.out.println("   Count: " + count);
        
        // 8. anyMatch(), allMatch(), noneMatch()
        System.out.println("\n8. Match operations:");
        boolean hasEven = numbers.stream().anyMatch(n -> n % 2 == 0);
        boolean allPositive = numbers.stream().allMatch(n -> n > 0);
        boolean noNegative = numbers.stream().noneMatch(n -> n < 0);
        System.out.println("   Has even number: " + hasEven);
        System.out.println("   All positive: " + allPositive);
        System.out.println("   No negative: " + noNegative);
        
        // 9. findFirst(), findAny()
        System.out.println("\n9. Find operations:");
        Optional<Integer> first = numbers.stream().findFirst();
        Optional<Integer> any = numbers.stream().findAny();
        System.out.println("   First element: " + first.orElse(null));
        System.out.println("   Any element: " + any.orElse(null));
        
        // 10. min(), max()
        System.out.println("\n10. Min/Max operations:");
        Optional<Integer> min = numbers.stream().min(Integer::compareTo);
        Optional<Integer> max = numbers.stream().max(Integer::compareTo);
        System.out.println("   Minimum: " + min.orElse(null));
        System.out.println("   Maximum: " + max.orElse(null));
        
        // 11. toArray()
        System.out.println("\n11. toArray() - Convert to array:");
        Integer[] array = numbers.stream()
            .filter(n -> n <= 5)
            .toArray(Integer[]::new);
        System.out.println("   Array: " + Arrays.toString(array));
    }
    
    // ============================================================================
    // PART 4: REAL-WORLD EXAMPLE - EMPLOYEE MANAGEMENT
    // ============================================================================
    
    private static void demonstrateEmployeeManagement() {
        System.out.println("\n" + "-".repeat(80));
        System.out.println("PART 4: REAL-WORLD EXAMPLE - EMPLOYEE MANAGEMENT SYSTEM");
        System.out.println("-".repeat(80));
        
        List<Employee> employees = Arrays.asList(
            new Employee(1, "Alice Johnson", 28, "Engineering", 75000),
            new Employee(2, "Bob Smith", 35, "Marketing", 65000),
            new Employee(3, "Charlie Brown", 42, "Engineering", 95000),
            new Employee(4, "Diana Prince", 30, "HR", 60000),
            new Employee(5, "Eve Davis", 25, "Engineering", 70000),
            new Employee(6, "Frank Miller", 50, "Management", 120000),
            new Employee(7, "Grace Lee", 33, "Marketing", 68000),
            new Employee(8, "Henry Wilson", 45, "Engineering", 98000)
        );
        
        // Query 1: Get all employees in Engineering
        System.out.println("\n1. Engineering Department Employees:");
        employees.stream()
            .filter(e -> "Engineering".equals(e.department()))
            .forEach(e -> System.out.println("   - " + e.name() + " (ID: " + e.id() + ")"));
        
        // Query 2: Get names of employees earning more than 70k
        System.out.println("\n2. High Earners (>$70k):");
        List<String> highEarners = employees.stream()
            .filter(e -> e.salary() > 70000)
            .map(Employee::name)
            .sorted()
            .collect(Collectors.toList());
        highEarners.forEach(name -> System.out.println("   - " + name));
        
        // Query 3: Average salary by department
        System.out.println("\n3. Average Salary by Department:");
        Map<String, Double> avgSalaryByDept = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::department,
                Collectors.averagingDouble(Employee::salary)
            ));
        avgSalaryByDept.forEach((dept, avg) -> 
            System.out.printf("   - %s: $%.2f%n", dept, avg));
        
        // Query 4: Oldest employee in each department
        System.out.println("\n4. Oldest Employee in Each Department:");
        Map<String, Optional<Employee>> oldestByDept = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::department,
                Collectors.maxBy(Comparator.comparingInt(Employee::age))
            ));
        oldestByDept.forEach((dept, empOpt) -> 
            empOpt.ifPresent(emp -> System.out.println(
                "   - " + dept + ": " + emp.name() + " (Age: " + emp.age() + ")")));
        
        // Query 5: Total salary expense
        System.out.println("\n5. Total Salary Expense:");
        double totalSalary = employees.stream()
            .mapToDouble(Employee::salary)
            .sum();
        System.out.printf("   Total: $%.2f%n", totalSalary);
        
        // Query 6: Count employees by department
        System.out.println("\n6. Employee Count by Department:");
        Map<String, Long> countByDept = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::department,
                Collectors.counting()
            ));
        countByDept.forEach((dept, count) -> 
            System.out.println("   - " + dept + ": " + count + " employees"));
        
        // Query 7: Get top 3 highest paid employees
        System.out.println("\n7. Top 3 Highest Paid Employees:");
        employees.stream()
            .sorted(Comparator.comparingDouble(Employee::salary).reversed())
            .limit(3)
            .forEach(e -> System.out.printf(
                "   - %s: $%.2f%n", e.name(), e.salary()));
        
        // Query 8: Check if all employees earn more than 50k
        System.out.println("\n8. Salary Analysis:");
        boolean allAbove50k = employees.stream()
            .allMatch(e -> e.salary() >= 50000);
        System.out.println("   All employees earn ≥ $50k: " + allAbove50k);
        
        // Query 9: Partition employees by age (under/over 35)
        System.out.println("\n9. Employees Partitioned by Age (35):");
        Map<Boolean, List<Employee>> partitioned = employees.stream()
            .collect(Collectors.partitioningBy(e -> e.age() < 35));
        System.out.println("   Under 35: " + partitioned.get(true).size() + " employees");
        System.out.println("   35 and over: " + partitioned.get(false).size() + " employees");
        
        // Query 10: Summary statistics for salaries
        System.out.println("\n10. Salary Statistics:");
        DoubleSummaryStatistics stats = employees.stream()
            .mapToDouble(Employee::salary)
            .summaryStatistics();
        System.out.printf("   Count: %d%n", stats.getCount());
        System.out.printf("   Min: $%.2f%n", stats.getMin());
        System.out.printf("   Max: $%.2f%n", stats.getMax());
        System.out.printf("   Average: $%.2f%n", stats.getAverage());
        System.out.printf("   Sum: $%.2f%n", stats.getSum());
    }
    
    // ============================================================================
    // PART 5: ADVANCED STREAM OPERATIONS
    // ============================================================================
    
    private static void demonstrateAdvancedOperations() {
        System.out.println("\n" + "-".repeat(80));
        System.out.println("PART 5: ADVANCED STREAM OPERATIONS");
        System.out.println("-".repeat(80));
        
        // 1. Chaining multiple operations
        System.out.println("\n1. Complex Pipeline:");
        List<String> words = Arrays.asList("java", "stream", "api", "functional", "programming");
        Map<Integer, List<String>> wordsByLength = words.stream()
            .filter(w -> w.length() > 3)
            .map(String::toUpperCase)
            .sorted()
            .collect(Collectors.groupingBy(String::length));
        System.out.println("   Words grouped by length: " + wordsByLength);
        
        // 2. FlatMap with real example
        System.out.println("\n2. FlatMap - Get all characters from words:");
        List<String> phrases = Arrays.asList("Hello World", "Java Streams");
        Set<String> uniqueChars = phrases.stream()
            .flatMap(phrase -> Arrays.stream(phrase.split("")))
            .filter(c -> !c.equals(" "))
            .collect(Collectors.toSet());
        System.out.println("   Unique characters: " + uniqueChars);
        
        // 3. Custom collector
        System.out.println("\n3. Custom Collector - Concatenate with separator:");
        String result = Stream.of("A", "B", "C", "D")
            .collect(Collectors.joining(" -> ", "[", "]"));
        System.out.println("   Result: " + result);
        
        // 4. Parallel streams
        System.out.println("\n4. Parallel Stream Processing:");
        long start = System.currentTimeMillis();
        long count = IntStream.rangeClosed(1, 1_000_000)
            .parallel()
            .filter(n -> n % 2 == 0)
            .count();
        long end = System.currentTimeMillis();
        System.out.println("   Even numbers count: " + count);
        System.out.println("   Time taken: " + (end - start) + "ms");
        
        // 5. takeWhile and dropWhile (Java 9+)
        System.out.println("\n5. takeWhile - Take elements while condition is true:");
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        List<Integer> taken = numbers.stream()
            .takeWhile(n -> n < 5)
            .collect(Collectors.toList());
        System.out.println("   Taken: " + taken);
        
        System.out.println("\n6. dropWhile - Drop elements while condition is true:");
        List<Integer> dropped = numbers.stream()
            .dropWhile(n -> n < 5)
            .collect(Collectors.toList());
        System.out.println("   Remaining: " + dropped);
    }
}

/**
 * Employee record for demonstration purposes.
 * Using Java 17+ record feature for immutable data.
 */
record Employee(int id, String name, int age, String department, double salary) {
    @Override
    public String toString() {
        return String.format("Employee{id=%d, name='%s', age=%d, dept='%s', salary=%.2f}", 
            id, name, age, department, salary);
    }
}
