package com.masterclass.functional;

import com.masterclass.functional.model.Employee;

import java.util.*;
import java.util.function.*;

/**
 * Comprehensive Demo of Functional Interfaces
 * 
 * Demonstrates all major functional interfaces with practical examples:
 * - Predicate, Function, Consumer, Supplier
 * - BiFunction, BiPredicate, BiConsumer
 * - UnaryOperator, BinaryOperator
 * - Method References
 * - Function Composition
 */
public class FunctionalInterfacesDemo {

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║  Functional Interfaces Demo              ║");
        System.out.println("╚═══════════════════════════════════════════╝\n");

        // Create sample data
        List<Employee> employees = createEmployees();

        // Run all demonstrations
        demonstratePredicate(employees);
        demonstrateFunction(employees);
        demonstrateConsumer(employees);
        demonstrateSupplier();
        demonstrateBiFunction();
        demonstrateBiPredicate();
        demonstrateBiConsumer();
        demonstrateUnaryOperator();
        demonstrateBinaryOperator();
        demonstrateMethodReferences(employees);
        demonstrateFunctionComposition();
        demonstrateRealWorldScenarios(employees);
    }

    // ==================== PART 1: Predicate ====================
    
    private static void demonstratePredicate(List<Employee> employees) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PART 1: Predicate<T> - Boolean Tests");
        System.out.println("=".repeat(50));

        // Simple predicates
        Predicate<Employee> isHighEarner = emp -> emp.getSalary() > 70000;
        Predicate<Employee> isActive = Employee::isActive;
        Predicate<Employee> isIT = emp -> "IT".equals(emp.getDepartment());
        Predicate<Employee> isSenior = emp -> emp.getAge() > 35;

        System.out.println("\n1. Simple Predicates:");
        System.out.println("   High earners (>70k):");
        employees.stream()
            .filter(isHighEarner)
            .forEach(emp -> System.out.println("   - " + emp.getName() + ": $" + emp.getSalary()));

        // Predicate composition (AND, OR, NEGATE)
        System.out.println("\n2. Predicate Composition:");
        
        // AND: High earner AND Active
        Predicate<Employee> highEarnerAndActive = isHighEarner.and(isActive);
        System.out.println("   High earners who are active:");
        employees.stream()
            .filter(highEarnerAndActive)
            .forEach(emp -> System.out.println("   - " + emp.getName()));

        // OR: IT department OR Senior
        Predicate<Employee> itOrSenior = isIT.or(isSenior);
        System.out.println("\n   IT department or Senior employees:");
        employees.stream()
            .filter(itOrSenior)
            .forEach(emp -> System.out.println("   - " + emp.getName() + 
                " (" + emp.getDepartment() + ", Age: " + emp.getAge() + ")"));

        // NEGATE: Not high earner
        Predicate<Employee> notHighEarner = isHighEarner.negate();
        System.out.println("\n   Regular salary employees:");
        long count = employees.stream()
            .filter(notHighEarner)
            .count();
        System.out.println("   Count: " + count);

        // Complex composition
        Predicate<Employee> complexFilter = isActive
            .and(isHighEarner)
            .and(emp -> emp.getAge() < 40);
        
        System.out.println("\n   Active, high-earning, young employees:");
        employees.stream()
            .filter(complexFilter)
            .forEach(emp -> System.out.println("   - " + emp.getName() + 
                ", Age: " + emp.getAge() + ", Salary: $" + emp.getSalary()));
    }

    // ==================== PART 2: Function ====================
    
    private static void demonstrateFunction(List<Employee> employees) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PART 2: Function<T, R> - Transformation");
        System.out.println("=".repeat(50));

        // Simple functions
        Function<Employee, String> getEmail = Employee::getEmail;
        Function<Employee, Double> getSalary = Employee::getSalary;
        Function<String, Integer> getLength = String::length;
        Function<Double, String> formatSalary = sal -> String.format("$%.2f", sal);

        System.out.println("\n1. Simple Functions:");
        System.out.println("   Employee emails:");
        employees.stream()
            .map(getEmail)
            .limit(3)
            .forEach(email -> System.out.println("   - " + email));

        System.out.println("\n   Formatted salaries:");
        employees.stream()
            .map(getSalary)
            .map(formatSalary)
            .limit(3)
            .forEach(salary -> System.out.println("   - " + salary));

        // Function composition with andThen
        System.out.println("\n2. Function Composition (andThen):");
        Function<Employee, String> getNameAndLength = 
            Employee::getName
            .andThen(name -> name + " (" + name.length() + " chars)");
        
        System.out.println("   Employee names with length:");
        employees.stream()
            .map(getNameAndLength)
            .limit(3)
            .forEach(result -> System.out.println("   - " + result));

        // Function composition with compose
        System.out.println("\n3. Function Composition (compose):");
        Function<String, String> addPrefix = str -> "Employee: " + str;
        Function<Employee, String> nameWithPrefix = 
            addPrefix.compose(Employee::getName);
        
        employees.stream()
            .map(nameWithPrefix)
            .limit(3)
            .forEach(result -> System.out.println("   - " + result));

        // Complex transformation pipeline
        System.out.println("\n4. Complex Transformation Pipeline:");
        Function<Employee, String> employeeInfo = 
            emp -> emp.getName()
            + " (" + emp.getDepartment() + ")"
            + " - $" + String.format("%.0f", emp.getSalary());
        
        System.out.println("   Employee summaries:");
        employees.stream()
            .map(employeeInfo)
            .forEach(info -> System.out.println("   - " + info));
    }

    // ==================== PART 3: Consumer ====================
    
    private static void demonstrateConsumer(List<Employee> employees) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PART 3: Consumer<T> - Side Effects");
        System.out.println("=".repeat(50));

        // Simple consumers
        Consumer<Employee> printName = emp -> 
            System.out.println("   Processing: " + emp.getName());
        
        Consumer<Employee> raiseSalary = emp -> 
            emp.setSalary(emp.getSalary() * 1.10);
        
        Consumer<Employee> sendEmail = emp -> 
            System.out.println("   📧 Email sent to: " + emp.getEmail());

        System.out.println("\n1. Simple Consumer:");
        employees.stream()
            .limit(2)
            .forEach(printName);

        // Consumer chaining with andThen
        System.out.println("\n2. Consumer Chaining (andThen):");
        Consumer<Employee> processEmployee = printName
            .andThen(raiseSalary)
            .andThen(emp -> System.out.println("   ✅ Salary raised to: $" + 
                String.format("%.2f", emp.getSalary())))
            .andThen(sendEmail);

        System.out.println("   Processing first employee:");
        processEmployee.accept(employees.get(0));

        // Multiple operations
        System.out.println("\n3. Multiple Side Effects:");
        List<String> processedNames = new ArrayList<>();
        Consumer<Employee> multipleEffects = emp -> {
            processedNames.add(emp.getName());
            System.out.println("   ✓ Processed: " + emp.getName());
        };

        employees.stream()
            .limit(3)
            .forEach(multipleEffects);
        
        System.out.println("   Total processed: " + processedNames.size());
    }

    // ==================== PART 4: Supplier ====================
    
    private static void demonstrateSupplier() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PART 4: Supplier<T> - Data Generation");
        System.out.println("=".repeat(50));

        // Simple suppliers
        Supplier<Double> randomSalary = () -> 40000 + Math.random() * 60000;
        Supplier<String> generateId = () -> "EMP-" + UUID.randomUUID().toString().substring(0, 8);
        Supplier<Long> currentTimestamp = System::currentTimeMillis;
        Supplier<Integer> randomAge = () -> 22 + new Random().nextInt(43);

        System.out.println("\n1. Random Data Generation:");
        System.out.println("   Generated IDs:");
        for (int i = 0; i < 3; i++) {
            System.out.println("   - " + generateId.get());
        }

        System.out.println("\n   Generated Salaries:");
        for (int i = 0; i < 3; i++) {
            System.out.println("   - $" + String.format("%.2f", randomSalary.get()));
        }

        // Lazy initialization
        System.out.println("\n2. Lazy Initialization:");
        Supplier<List<Employee>> lazyEmployeeList = ArrayList::new;
        System.out.println("   Supplier created (no list created yet)");
        
        List<Employee> empList = lazyEmployeeList.get();
        System.out.println("   List created: " + empList.getClass().getSimpleName());

        // Factory pattern
        System.out.println("\n3. Factory Pattern:");
        Supplier<Employee> employeeFactory = () -> new Employee(
            (long) (Math.random() * 1000),
            "Generated Employee",
            "IT",
            randomSalary.get(),
            randomAge.get(),
            "employee@company.com",
            true
        );

        Employee newEmp = employeeFactory.get();
        System.out.println("   Created: " + newEmp.getName() + 
            ", Age: " + newEmp.getAge() + 
            ", Salary: $" + String.format("%.2f", newEmp.getSalary()));
    }

    // ==================== PART 5: BiFunction ====================
    
    private static void demonstrateBiFunction() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PART 5: BiFunction<T, U, R> - Two Arguments");
        System.out.println("=".repeat(50));

        // Simple BiFunction
        BiFunction<String, Integer, Employee> createEmployee = 
            (name, age) -> new Employee(1L, name, "IT", 50000, age, 
                name.toLowerCase() + "@company.com", true);

        BiFunction<Double, Double, Double> calculateBonus = 
            (salary, rate) -> salary * rate;

        BiFunction<String, String, String> concatenate = 
            (first, last) -> first + " " + last;

        System.out.println("\n1. Creating Objects:");
        Employee emp = createEmployee.apply("John Smith", 30);
        System.out.println("   Created: " + emp.getName() + 
            ", Age: " + emp.getAge() + ", Email: " + emp.getEmail());

        System.out.println("\n2. Calculations:");
        double bonus = calculateBonus.apply(75000.0, 0.15);
        System.out.println("   Bonus calculation: $75000 * 0.15 = $" + 
            String.format("%.2f", bonus));

        System.out.println("\n3. String Operations:");
        String fullName = concatenate.apply("Alice", "Johnson");
        System.out.println("   Full name: " + fullName);

        // Complex BiFunction
        BiFunction<Employee, Double, Employee> applyRaise = (employee, percentage) -> {
            employee.setSalary(employee.getSalary() * (1 + percentage));
            return employee;
        };

        Employee raisedEmp = applyRaise.apply(emp, 0.10);
        System.out.println("\n4. Apply Raise:");
        System.out.println("   New salary: $" + String.format("%.2f", raisedEmp.getSalary()));
    }

    // ==================== PART 6: BiPredicate ====================
    
    private static void demonstrateBiPredicate() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PART 6: BiPredicate<T, U> - Two Argument Tests");
        System.out.println("=".repeat(50));

        BiPredicate<Integer, Integer> isGreater = (a, b) -> a > b;
        BiPredicate<String, String> startsWith = String::startsWith;
        BiPredicate<Employee, Double> salaryGreaterThan = 
            (emp, threshold) -> emp.getSalary() > threshold;

        System.out.println("\n1. Number Comparisons:");
        System.out.println("   10 > 5: " + isGreater.test(10, 5));
        System.out.println("   3 > 7: " + isGreater.test(3, 7));

        System.out.println("\n2. String Operations:");
        System.out.println("   'Hello' starts with 'Hel': " + startsWith.test("Hello", "Hel"));
        System.out.println("   'World' starts with 'Wor': " + startsWith.test("World", "Wor"));

        System.out.println("\n3. Employee Validation:");
        Employee testEmp = new Employee(1L, "Test", "IT", 60000, 30, 
            "test@company.com", true);
        System.out.println("   Salary > 50000: " + salaryGreaterThan.test(testEmp, 50000.0));
        System.out.println("   Salary > 70000: " + salaryGreaterThan.test(testEmp, 70000.0));
    }

    // ==================== PART 7: BiConsumer ====================
    
    private static void demonstrateBiConsumer() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PART 7: BiConsumer<T, U> - Two Argument Side Effects");
        System.out.println("=".repeat(50));

        BiConsumer<String, Integer> printInfo = 
            (name, age) -> System.out.println("   " + name + " is " + age + " years old");

        BiConsumer<Employee, String> updateDepartment = 
            (emp, dept) -> emp.setDepartment(dept);

        Map<String, Integer> salaryMap = new HashMap<>();
        BiConsumer<String, Integer> addToMap = salaryMap::put;

        System.out.println("\n1. Print Information:");
        printInfo.accept("Alice", 28);
        printInfo.accept("Bob", 35);

        System.out.println("\n2. Update Employee:");
        Employee emp = new Employee(1L, "Charlie", "IT", 50000, 30, 
            "charlie@company.com", true);
        System.out.println("   Before: " + emp.getDepartment());
        updateDepartment.accept(emp, "Engineering");
        System.out.println("   After: " + emp.getDepartment());

        System.out.println("\n3. Map Operations:");
        addToMap.accept("Engineer", 70000);
        addToMap.accept("Manager", 90000);
        addToMap.accept("Analyst", 55000);
        
        System.out.println("   Salary map:");
        salaryMap.forEach((role, salary) -> 
            System.out.println("   - " + role + ": $" + salary));
    }

    // ==================== PART 8: UnaryOperator ====================
    
    private static void demonstrateUnaryOperator() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PART 8: UnaryOperator<T> - Same Type Transform");
        System.out.println("=".repeat(50));

        UnaryOperator<Integer> square = n -> n * n;
        UnaryOperator<String> toUpperCase = String::toUpperCase;
        UnaryOperator<Double> addTax = price -> price * 1.08;
        UnaryOperator<String> trimAndClean = str -> str.trim().replaceAll("\\s+", " ");

        System.out.println("\n1. Number Operations:");
        System.out.println("   Square of 5: " + square.apply(5));
        System.out.println("   Square of 12: " + square.apply(12));

        System.out.println("\n2. String Operations:");
        System.out.println("   Uppercase: " + toUpperCase.apply("hello world"));

        System.out.println("\n3. Price Calculations:");
        System.out.println("   Price $100 with tax: $" + 
            String.format("%.2f", addTax.apply(100.0)));

        System.out.println("\n4. String Cleaning:");
        String messy = "  hello    world  ";
        System.out.println("   Before: '" + messy + "'");
        System.out.println("   After: '" + trimAndClean.apply(messy) + "'");
    }

    // ==================== PART 9: BinaryOperator ====================
    
    private static void demonstrateBinaryOperator() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PART 9: BinaryOperator<T> - Two Same Type Args");
        System.out.println("=".repeat(50));

        BinaryOperator<Integer> sum = (a, b) -> a + b;
        BinaryOperator<Integer> max = Math::max;
        BinaryOperator<String> concat = (s1, s2) -> s1 + " " + s2;
        BinaryOperator<Double> multiply = (a, b) -> a * b;

        System.out.println("\n1. Math Operations:");
        System.out.println("   Sum: 5 + 3 = " + sum.apply(5, 3));
        System.out.println("   Max: max(10, 7) = " + max.apply(10, 7));
        System.out.println("   Multiply: 4.5 * 2.0 = " + multiply.apply(4.5, 2.0));

        System.out.println("\n2. String Operations:");
        System.out.println("   Concat: " + concat.apply("Hello", "World"));

        System.out.println("\n3. Reduce Operations:");
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        Integer total = numbers.stream().reduce(0, sum);
        System.out.println("   Sum of [1,2,3,4,5] = " + total);

        Integer maximum = numbers.stream().reduce(Integer.MIN_VALUE, max);
        System.out.println("   Max of [1,2,3,4,5] = " + maximum);
    }

    // ==================== PART 10: Method References ====================
    
    private static void demonstrateMethodReferences(List<Employee> employees) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PART 10: Method References");
        System.out.println("=".repeat(50));

        System.out.println("\n1. Static Method Reference:");
        Function<String, Integer> parseInt = Integer::parseInt;
        System.out.println("   Parse '123': " + parseInt.apply("123"));

        System.out.println("\n2. Instance Method Reference:");
        String prefix = "Hello, ";
        Function<String, String> greeter = prefix::concat;
        System.out.println("   Greeting: " + greeter.apply("Alice"));

        System.out.println("\n3. Instance Method of Arbitrary Object:");
        List<String> names = Arrays.asList("alice", "bob", "charlie");
        System.out.println("   Original: " + names);
        List<String> upper = names.stream()
            .map(String::toUpperCase)
            .toList();
        System.out.println("   Uppercase: " + upper);

        System.out.println("\n4. Constructor Reference:");
        Supplier<ArrayList<String>> listSupplier = ArrayList::new;
        ArrayList<String> newList = listSupplier.get();
        System.out.println("   Created list: " + newList.getClass().getSimpleName());

        System.out.println("\n5. Using with Employees:");
        employees.stream()
            .map(Employee::getName)
            .limit(3)
            .forEach(System.out::println);
    }

    // ==================== PART 11: Function Composition ====================
    
    private static void demonstrateFunctionComposition() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PART 11: Function Composition");
        System.out.println("=".repeat(50));

        Function<String, String> trimmer = String::trim;
        Function<String, String> upperCaser = String::toUpperCase;
        Function<String, String> addExclamation = s -> s + "!";

        System.out.println("\n1. AndThen (Left to Right):");
        Function<String, String> pipeline = trimmer
            .andThen(upperCaser)
            .andThen(addExclamation);
        
        String input1 = "  hello world  ";
        System.out.println("   Input: '" + input1 + "'");
        System.out.println("   Output: '" + pipeline.apply(input1) + "'");

        System.out.println("\n2. Compose (Right to Left):");
        Function<String, String> composed = addExclamation
            .compose(upperCaser)
            .compose(trimmer);
        
        String input2 = "  goodbye  ";
        System.out.println("   Input: '" + input2 + "'");
        System.out.println("   Output: '" + composed.apply(input2) + "'");

        System.out.println("\n3. Complex Pipeline:");
        Function<Integer, Integer> multiplyBy2 = n -> n * 2;
        Function<Integer, Integer> add10 = n -> n + 10;
        Function<Integer, Integer> square = n -> n * n;

        Function<Integer, Integer> complexPipeline = multiplyBy2
            .andThen(add10)
            .andThen(square);
        
        int number = 5;
        System.out.println("   Input: " + number);
        System.out.println("   (5 * 2) = 10");
        System.out.println("   (10 + 10) = 20");
        System.out.println("   (20 * 20) = " + complexPipeline.apply(number));
    }

    // ==================== PART 12: Real World Scenarios ====================
    
    private static void demonstrateRealWorldScenarios(List<Employee> employees) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PART 12: Real-World Scenarios");
        System.out.println("=".repeat(50));

        System.out.println("\n1. Employee Processing Pipeline:");
        
        // Validation
        Predicate<Employee> isValid = emp -> 
            emp.getSalary() > 0 && emp.getAge() >= 18 && emp.isActive();
        
        // Transformation
        Function<Employee, String> toSummary = emp ->
            String.format("%s (%s) - $%.0f", 
                emp.getName(), emp.getDepartment(), emp.getSalary());
        
        // Side effect
        Consumer<String> logger = summary -> 
            System.out.println("   ✓ " + summary);

        System.out.println("   Valid active employees:");
        employees.stream()
            .filter(isValid)
            .map(toSummary)
            .forEach(logger);

        System.out.println("\n2. Salary Analysis:");
        DoubleBinaryOperator averageOperator = (sum, count) -> sum / count;
        
        double totalSalary = employees.stream()
            .mapToDouble(Employee::getSalary)
            .sum();
        
        double avgSalary = totalSalary / employees.size();
        
        System.out.println("   Total Salary: $" + String.format("%.2f", totalSalary));
        System.out.println("   Average Salary: $" + String.format("%.2f", avgSalary));

        System.out.println("\n3. Department Grouping:");
        Map<String, List<Employee>> byDepartment = new HashMap<>();
        
        Consumer<Employee> groupByDept = emp -> {
            byDepartment.computeIfAbsent(emp.getDepartment(), k -> new ArrayList<>())
                       .add(emp);
        };
        
        employees.forEach(groupByDept);
        
        byDepartment.forEach((dept, emps) -> 
            System.out.println("   " + dept + ": " + emps.size() + " employees"));

        System.out.println("\n4. Bonus Calculation:");
        Function<Employee, Double> calculateBonus = emp -> {
            double bonus = emp.getSalary() * 0.10; // 10% base
            if (emp.getAge() > 35) bonus *= 1.2;   // 20% extra for seniors
            if (emp.getSalary() > 70000) bonus *= 1.15; // 15% extra for high earners
            return bonus;
        };

        System.out.println("   Employee bonuses:");
        employees.stream()
            .limit(5)
            .forEach(emp -> {
                double bonus = calculateBonus.apply(emp);
                System.out.println("   - " + emp.getName() + ": $" + 
                    String.format("%.2f", bonus));
            });
    }

    // ==================== Helper Methods ====================
    
    private static List<Employee> createEmployees() {
        return Arrays.asList(
            new Employee(1L, "Alice Johnson", "IT", 75000, 28, "alice@company.com", true),
            new Employee(2L, "Bob Smith", "HR", 55000, 35, "bob@company.com", true),
            new Employee(3L, "Charlie Brown", "IT", 82000, 42, "charlie@company.com", true),
            new Employee(4L, "Diana Prince", "Finance", 68000, 31, "diana@company.com", true),
            new Employee(5L, "Eve Wilson", "IT", 95000, 38, "eve@company.com", true),
            new Employee(6L, "Frank Miller", "HR", 48000, 26, "frank@company.com", false),
            new Employee(7L, "Grace Lee", "Finance", 71000, 33, "grace@company.com", true),
            new Employee(8L, "Henry Davis", "IT", 78000, 29, "henry@company.com", true),
            new Employee(9L, "Iris Chen", "Marketing", 62000, 27, "iris@company.com", true),
            new Employee(10L, "Jack Thompson", "Finance", 89000, 45, "jack@company.com", true)
        );
    }
}
