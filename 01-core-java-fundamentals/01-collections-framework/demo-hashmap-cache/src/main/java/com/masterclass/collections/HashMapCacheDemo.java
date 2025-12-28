package com.masterclass.collections;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Comprehensive demonstration of HashMap operations through a caching system.
 * 
 * HashMap is the most commonly used Map implementation for key-value storage.
 * 
 * Key Characteristics:
 * - No duplicate keys allowed (values can be duplicate)
 * - Allows one null key and multiple null values
 * - No guaranteed order
 * - O(1) average time complexity for get/put
 * - Best for: Fast key-value lookups
 * 
 * Real-world uses:
 * - Caching frequently accessed data
 * - Configuration storage
 * - Counting occurrences
 * - Lookup tables
 * 
 * @author Spring Boot Microservices Masterclass
 */
public class HashMapCacheDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("      HashMap & Caching Demonstration");
        System.out.println("=".repeat(60));
        System.out.println();

        // Example 1: Basic HashMap Operations
        basicHashMapOperations();

        System.out.println("\n" + "=".repeat(60) + "\n");

        // Example 2: Iterating HashMap
        iteratingHashMap();

        System.out.println("\n" + "=".repeat(60) + "\n");

        // Example 3: Common HashMap Patterns
        commonHashMapPatterns();

        System.out.println("\n" + "=".repeat(60) + "\n");

        // Example 4: Real-World - User Cache
        userCacheExample();

        System.out.println("\n" + "=".repeat(60) + "\n");

        // Example 5: Real-World - Word Counter
        wordCounterExample();

        System.out.println("\n" + "=".repeat(60) + "\n");

        // Example 6: Product Catalog with LinkedHashMap
        linkedHashMapExample();

        System.out.println("\n" + "=".repeat(60) + "\n");

        // Example 7: Sorted Map with TreeMap
        treeMapExample();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("           Demo Completed Successfully!");
        System.out.println("=".repeat(60));
    }

    /**
     * Example 1: Basic HashMap operations
     */
    private static void basicHashMapOperations() {
        System.out.println("📚 Example 1: Basic HashMap Operations");
        System.out.println("-".repeat(40));

        // Creating HashMap
        Map<String, Integer> scores = new HashMap<>();

        // Adding elements
        scores.put("Alice", 95);
        scores.put("Bob", 87);
        scores.put("Charlie", 92);
        System.out.println("Initial scores: " + scores);

        // Getting elements
        Integer aliceScore = scores.get("Alice");
        System.out.println("Alice's score: " + aliceScore);

        // Get with default value (Java 8+)
        Integer davidScore = scores.getOrDefault("David", 0);
        System.out.println("David's score (not found): " + davidScore);

        // Update existing key
        scores.put("Alice", 98);  // Replaces old value
        System.out.println("Updated Alice's score: " + scores.get("Alice"));

        // Put if absent (Java 8+)
        scores.putIfAbsent("Alice", 100);  // Won't update (key exists)
        scores.putIfAbsent("Eve", 89);     // Will add (key doesn't exist)
        System.out.println("After putIfAbsent: " + scores);

        // Remove elements
        Integer removed = scores.remove("Bob");
        System.out.println("Removed Bob (score: " + removed + "): " + scores);

        // Check operations
        System.out.println("Contains key 'Charlie': " + scores.containsKey("Charlie"));
        System.out.println("Contains value 98: " + scores.containsValue(98));
        System.out.println("Size: " + scores.size());

        System.out.println("\n💡 HashMap provides O(1) average time for get/put!");
    }

    /**
     * Example 2: Different ways to iterate HashMap
     */
    private static void iteratingHashMap() {
        System.out.println("🔄 Example 2: Iterating HashMap");
        System.out.println("-".repeat(40));

        Map<String, String> countries = new HashMap<>();
        countries.put("US", "United States");
        countries.put("UK", "United Kingdom");
        countries.put("IN", "India");
        countries.put("JP", "Japan");

        // Method 1: Enhanced for loop with entrySet (most efficient)
        System.out.println("1. Using entrySet:");
        for (Map.Entry<String, String> entry : countries.entrySet()) {
            System.out.println("   " + entry.getKey() + " → " + entry.getValue());
        }

        // Method 2: forEach with lambda (Java 8+, most concise)
        System.out.println("\n2. Using forEach:");
        countries.forEach((code, name) -> 
            System.out.println("   " + code + " → " + name)
        );

        // Method 3: Iterate over keys
        System.out.println("\n3. Using keySet:");
        for (String code : countries.keySet()) {
            System.out.println("   " + code + " → " + countries.get(code));
        }

        // Method 4: Iterate over values only
        System.out.println("\n4. Using values:");
        for (String name : countries.values()) {
            System.out.println("   " + name);
        }

        // Method 5: Stream API (Java 8+)
        System.out.println("\n5. Using Stream API:");
        countries.entrySet().stream()
            .filter(entry -> entry.getKey().length() == 2)
            .forEach(entry -> 
                System.out.println("   " + entry.getKey() + " → " + entry.getValue())
            );

        System.out.println("\n💡 Use entrySet() for best performance when needing both key and value!");
    }

    /**
     * Example 3: Common HashMap patterns
     */
    private static void commonHashMapPatterns() {
        System.out.println("🔧 Example 3: Common HashMap Patterns");
        System.out.println("-".repeat(40));

        Map<String, Integer> inventory = new HashMap<>();

        // Pattern 1: Increment counter
        String item = "apple";
        inventory.put(item, inventory.getOrDefault(item, 0) + 1);
        inventory.put(item, inventory.getOrDefault(item, 0) + 1);
        System.out.println("After adding apples twice: " + inventory);

        // Pattern 2: Compute if absent (create on first access)
        Map<String, List<String>> groupedData = new HashMap<>();
        groupedData.computeIfAbsent("fruits", k -> new ArrayList<>()).add("apple");
        groupedData.computeIfAbsent("fruits", k -> new ArrayList<>()).add("banana");
        groupedData.computeIfAbsent("vegetables", k -> new ArrayList<>()).add("carrot");
        System.out.println("Grouped data: " + groupedData);

        // Pattern 3: Merge values (Java 8+)
        Map<String, Integer> map1 = new HashMap<>(Map.of("a", 1, "b", 2));
        Map<String, Integer> map2 = new HashMap<>(Map.of("b", 3, "c", 4));
        
        map2.forEach((key, value) -> 
            map1.merge(key, value, Integer::sum)  // Sum if key exists, else put new
        );
        System.out.println("Merged map: " + map1);

        // Pattern 4: Replace operations
        Map<String, Integer> prices = new HashMap<>(Map.of(
            "apple", 10,
            "banana", 5
        ));
        prices.replace("apple", 12);  // Replace if exists
        prices.replace("cherry", 8);   // Does nothing (key doesn't exist)
        System.out.println("After replace: " + prices);

        // Pattern 5: Bulk operations
        prices.replaceAll((item2, price) -> price + 1);  // Increase all prices by 1
        System.out.println("After price increase: " + prices);

        System.out.println("\n💡 Master these patterns - they're used everywhere in real code!");
    }

    /**
     * Example 4: Real-world - User Cache System
     */
    private static void userCacheExample() {
        System.out.println("🌍 Example 4: Real-World - User Cache");
        System.out.println("-".repeat(40));

        UserCache userCache = new UserCache();

        // Simulate user requests
        System.out.println("Fetching user 1 (first time - from DB):");
        User user1 = userCache.getUser(1L);
        System.out.println("  → " + user1);

        System.out.println("\nFetching user 1 again (from cache):");
        User user1Again = userCache.getUser(1L);
        System.out.println("  → " + user1Again);

        System.out.println("\nFetching user 2 (first time - from DB):");
        User user2 = userCache.getUser(2L);
        System.out.println("  → " + user2);

        System.out.println("\nCache statistics:");
        System.out.println("  Cache size: " + userCache.getCacheSize());
        System.out.println("  Cache hits: " + userCache.getCacheHits());
        System.out.println("  DB queries: " + userCache.getDbQueries());

        // Invalidate cache
        userCache.invalidateUser(1L);
        System.out.println("\nAfter invalidating user 1:");
        System.out.println("  Cache size: " + userCache.getCacheSize());

        System.out.println("\n💡 Caching reduces database load and improves performance!");
    }

    /**
     * Example 5: Real-world - Word frequency counter
     */
    private static void wordCounterExample() {
        System.out.println("📊 Example 5: Real-World - Word Counter");
        System.out.println("-".repeat(40));

        String text = "java is great java is powerful spring boot uses java " +
                     "java spring boot is awesome spring makes java easier";

        WordCounter counter = new WordCounter();
        Map<String, Integer> wordCounts = counter.countWords(text);

        System.out.println("Word frequencies:");
        wordCounts.forEach((word, count) -> 
            System.out.println("  " + word + ": " + count + " time(s)")
        );

        System.out.println("\nTop 3 most frequent words:");
        counter.getTopWords(wordCounts, 3).forEach(entry ->
            System.out.println("  " + entry.getKey() + ": " + entry.getValue())
        );

        System.out.println("\n💡 HashMap perfect for counting/frequency analysis!");
    }

    /**
     * Example 6: LinkedHashMap maintains insertion order
     */
    private static void linkedHashMapExample() {
        System.out.println("📦 Example 6: LinkedHashMap - Ordered Catalog");
        System.out.println("-".repeat(40));

        // LinkedHashMap maintains insertion order
        Map<String, Double> productCatalog = new LinkedHashMap<>();
        productCatalog.put("Laptop", 999.99);
        productCatalog.put("Mouse", 29.99);
        productCatalog.put("Keyboard", 79.99);
        productCatalog.put("Monitor", 299.99);

        System.out.println("Product catalog (insertion order maintained):");
        productCatalog.forEach((product, price) ->
            System.out.println("  " + product + ": $" + price)
        );

        // LRU Cache using LinkedHashMap
        Map<String, String> lruCache = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return size() > 3;  // Keep only 3 most recent
            }
        };

        lruCache.put("1", "First");
        lruCache.put("2", "Second");
        lruCache.put("3", "Third");
        System.out.println("\nLRU Cache: " + lruCache);

        lruCache.get("1");  // Access "1" - moves to end
        lruCache.put("4", "Fourth");  // Evicts least recently used
        System.out.println("After access and new entry: " + lruCache);

        System.out.println("\n💡 LinkedHashMap perfect for LRU caches and ordered catalogs!");
    }

    /**
     * Example 7: TreeMap maintains sorted order
     */
    private static void treeMapExample() {
        System.out.println("🌳 Example 7: TreeMap - Sorted Leaderboard");
        System.out.println("-".repeat(40));

        // TreeMap keeps entries sorted by key
        Map<Integer, String> leaderboard = new TreeMap<>(Collections.reverseOrder());
        leaderboard.put(95, "Alice");
        leaderboard.put(87, "Bob");
        leaderboard.put(92, "Charlie");
        leaderboard.put(98, "Diana");
        leaderboard.put(89, "Eve");

        System.out.println("Leaderboard (sorted by score descending):");
        leaderboard.forEach((score, name) ->
            System.out.println("  " + name + ": " + score)
        );

        // NavigableMap operations (TreeMap implements NavigableMap)
        TreeMap<String, Integer> sortedMap = new TreeMap<>();
        sortedMap.put("apple", 5);
        sortedMap.put("banana", 3);
        sortedMap.put("cherry", 8);
        sortedMap.put("date", 2);

        System.out.println("\nNavigableMap operations:");
        System.out.println("  First entry: " + sortedMap.firstEntry());
        System.out.println("  Last entry: " + sortedMap.lastEntry());
        System.out.println("  Entries >= 'c': " + sortedMap.tailMap("c"));
        System.out.println("  Entries < 'c': " + sortedMap.headMap("c"));

        System.out.println("\n💡 TreeMap perfect for sorted data and range queries!");
    }

    // ==================== Helper Classes ====================

    /**
     * User entity
     */
    static class User {
        private Long id;
        private String name;
        private String email;

        public User(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        @Override
        public String toString() {
            return "User{id=" + id + ", name='" + name + "', email='" + email + "'}";
        }
    }

    /**
     * Simple user cache implementation
     */
    static class UserCache {
        private Map<Long, User> cache = new HashMap<>();
        private int cacheHits = 0;
        private int dbQueries = 0;

        public User getUser(Long id) {
            // Check cache first
            if (cache.containsKey(id)) {
                cacheHits++;
                System.out.println("  ✓ Cache HIT for user " + id);
                return cache.get(id);
            }

            // Cache miss - fetch from "database"
            dbQueries++;
            System.out.println("  ⚠ Cache MISS - querying database...");
            User user = fetchFromDatabase(id);
            
            // Store in cache
            cache.put(id, user);
            return user;
        }

        private User fetchFromDatabase(Long id) {
            // Simulate database query
            try {
                Thread.sleep(100);  // Simulate latency
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            return new User(id, "User" + id, "user" + id + "@example.com");
        }

        public void invalidateUser(Long id) {
            cache.remove(id);
            System.out.println("  ✓ Invalidated cache for user " + id);
        }

        public int getCacheSize() {
            return cache.size();
        }

        public int getCacheHits() {
            return cacheHits;
        }

        public int getDbQueries() {
            return dbQueries;
        }
    }

    /**
     * Word frequency counter
     */
    static class WordCounter {
        public Map<String, Integer> countWords(String text) {
            Map<String, Integer> wordCount = new HashMap<>();
            
            String[] words = text.toLowerCase().split("\\s+");
            
            for (String word : words) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
            
            return wordCount;
        }

        public List<Map.Entry<String, Integer>> getTopWords(Map<String, Integer> wordCount, int n) {
            return wordCount.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(n)
                .toList();
        }
    }
}
