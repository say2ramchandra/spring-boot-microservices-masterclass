# Java I/O and NIO

> **File handling and non-blocking I/O in Java**

## 📚 Table of Contents

1. [Introduction](#introduction)
2. [Traditional I/O Streams](#traditional-io-streams)
3. [java.nio.file API](#javaniofile-api)
4. [Files Utility Class](#files-utility-class)
5. [NIO Channels and Buffers](#nio-channels-and-buffers)
6. [File Watching](#file-watching)
7. [Memory-Mapped Files](#memory-mapped-files)
8. [Best Practices](#best-practices)
9. [Spring Boot Integration](#spring-boot-integration)

---

## Introduction

### I/O vs NIO

| Feature | I/O (java.io) | NIO (java.nio) |
|---------|---------------|----------------|
| Style | Stream-oriented | Buffer-oriented |
| Blocking | Blocking | Can be non-blocking |
| Channels | No | Yes |
| Selectors | No | Yes (multiplexing) |
| Use case | Simple file ops | High-performance, network |

### When to Use What

- **java.io streams**: Simple reading/writing, small files
- **java.nio.file.Files**: File operations, path handling (preferred)
- **NIO channels/buffers**: Large files, network I/O, performance-critical

---

## Traditional I/O Streams

### Reading Files

```java
// Character streams (text files)
try (BufferedReader reader = new BufferedReader(
        new FileReader("data.txt"))) {
    String line;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
}

// Byte streams (binary files)
try (FileInputStream fis = new FileInputStream("image.png")) {
    byte[] buffer = new byte[1024];
    int bytesRead;
    while ((bytesRead = fis.read(buffer)) != -1) {
        // process bytes
    }
}
```

### Writing Files

```java
// Character streams
try (BufferedWriter writer = new BufferedWriter(
        new FileWriter("output.txt"))) {
    writer.write("Hello, World!");
    writer.newLine();
    writer.write("Line 2");
}

// Byte streams
try (FileOutputStream fos = new FileOutputStream("data.bin")) {
    fos.write(new byte[] {0x48, 0x65, 0x6C, 0x6C, 0x6F});
}
```

### Try-with-Resources (Essential!)

```java
// ALWAYS use try-with-resources for I/O
try (
    FileInputStream fis = new FileInputStream("input.txt");
    BufferedInputStream bis = new BufferedInputStream(fis);
    FileOutputStream fos = new FileOutputStream("output.txt");
    BufferedOutputStream bos = new BufferedOutputStream(fos)
) {
    int data;
    while ((data = bis.read()) != -1) {
        bos.write(data);
    }
}  // All streams automatically closed!
```

---

## java.nio.file API

### Path Class (Modern File Handling)

```java
import java.nio.file.*;

// Creating paths
Path path1 = Path.of("data", "users.txt");           // data/users.txt
Path path2 = Path.of("/home/user/documents");        // Absolute
Path path3 = Paths.get("config.json");               // Alternative

// Path operations
Path absolute = path1.toAbsolutePath();
Path parent = path1.getParent();                     // data
Path fileName = path1.getFileName();                 // users.txt
int nameCount = path1.getNameCount();                // 2

// Resolving paths
Path base = Path.of("/home/user");
Path resolved = base.resolve("documents/file.txt");  // /home/user/documents/file.txt

// Normalizing paths
Path messy = Path.of("/home/user/../user/./docs");
Path clean = messy.normalize();                      // /home/user/docs

// Relative paths
Path from = Path.of("/home/user");
Path to = Path.of("/home/user/docs/file.txt");
Path relative = from.relativize(to);                 // docs/file.txt
```

### Path Comparisons

```java
Path p1 = Path.of("data/users.txt");
Path p2 = Path.of("data/../data/users.txt");

p1.equals(p2);                                       // false (different strings)
p1.equals(p2.normalize());                           // true
Files.isSameFile(p1, p2);                           // true (same actual file)
```

---

## Files Utility Class

### Reading Files (Modern Way - Preferred!)

```java
import java.nio.file.*;
import java.nio.charset.StandardCharsets;

// Read entire file as string
String content = Files.readString(Path.of("data.txt"));

// Read all lines
List<String> lines = Files.readAllLines(Path.of("data.txt"));

// Read all bytes
byte[] bytes = Files.readAllBytes(Path.of("image.png"));

// Stream lines (lazy - for large files)
try (Stream<String> stream = Files.lines(Path.of("large.csv"))) {
    stream.filter(line -> line.startsWith("ERROR"))
          .forEach(System.out::println);
}

// With specific charset
List<String> lines = Files.readAllLines(
    Path.of("data.txt"), 
    StandardCharsets.UTF_8
);
```

### Writing Files

```java
// Write string
Files.writeString(Path.of("output.txt"), "Hello, World!");

// Write lines
List<String> lines = List.of("Line 1", "Line 2", "Line 3");
Files.write(Path.of("output.txt"), lines);

// Write bytes
byte[] data = {0x48, 0x65, 0x6C, 0x6C, 0x6F};
Files.write(Path.of("output.bin"), data);

// Append to file
Files.writeString(
    Path.of("log.txt"), 
    "New log entry\n",
    StandardOpenOption.APPEND,
    StandardOpenOption.CREATE
);
```

### File Operations

```java
// Check existence
boolean exists = Files.exists(path);
boolean notExists = Files.notExists(path);

// File attributes
boolean isDirectory = Files.isDirectory(path);
boolean isRegularFile = Files.isRegularFile(path);
boolean isReadable = Files.isReadable(path);
boolean isWritable = Files.isWritable(path);
long size = Files.size(path);

// Create directory
Files.createDirectory(Path.of("newdir"));
Files.createDirectories(Path.of("parent/child/grandchild"));

// Create file
Files.createFile(Path.of("new.txt"));

// Copy file
Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

// Move file (rename)
Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);

// Delete
Files.delete(path);                    // Throws if not exists
Files.deleteIfExists(path);            // Returns boolean
```

### Walking Directory Tree

```java
// Walk all files
try (Stream<Path> paths = Files.walk(Path.of("src"))) {
    paths.filter(Files::isRegularFile)
         .filter(p -> p.toString().endsWith(".java"))
         .forEach(System.out::println);
}

// Walk with depth limit
try (Stream<Path> paths = Files.walk(Path.of("src"), 2)) {
    paths.forEach(System.out::println);
}

// Find files
try (Stream<Path> paths = Files.find(
        Path.of("src"), 
        10, 
        (path, attrs) -> attrs.isRegularFile() && 
                         path.toString().endsWith(".java"))) {
    paths.forEach(System.out::println);
}

// List directory (non-recursive)
try (Stream<Path> paths = Files.list(Path.of("src"))) {
    paths.forEach(System.out::println);
}
```

### Temporary Files

```java
// Create temp file
Path tempFile = Files.createTempFile("prefix_", ".tmp");
// e.g., /tmp/prefix_1234567890.tmp

// Create temp file in specific directory
Path tempFile = Files.createTempFile(
    Path.of("/custom/temp"), 
    "data_", 
    ".json"
);

// Create temp directory
Path tempDir = Files.createTempDirectory("myapp_");
```

---

## NIO Channels and Buffers

### Buffers

```java
import java.nio.*;

// Create buffer
ByteBuffer buffer = ByteBuffer.allocate(1024);  // Heap buffer
ByteBuffer direct = ByteBuffer.allocateDirect(1024);  // Direct (off-heap)

// Write to buffer
buffer.put((byte) 65);
buffer.putInt(12345);
buffer.put("Hello".getBytes());

// Flip for reading (position=0, limit=current position)
buffer.flip();

// Read from buffer
byte b = buffer.get();
int i = buffer.getInt();

// Common operations
buffer.rewind();    // position = 0 (re-read)
buffer.clear();     // position = 0, limit = capacity (ready for write)
buffer.compact();   // Copy remaining to start, ready for more writes
```

### FileChannel

```java
import java.nio.channels.*;

// Read using channel
try (FileChannel channel = FileChannel.open(
        Path.of("data.txt"), StandardOpenOption.READ)) {
    
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    int bytesRead = channel.read(buffer);
    
    buffer.flip();
    while (buffer.hasRemaining()) {
        System.out.print((char) buffer.get());
    }
}

// Write using channel
try (FileChannel channel = FileChannel.open(
        Path.of("output.txt"), 
        StandardOpenOption.WRITE, 
        StandardOpenOption.CREATE)) {
    
    ByteBuffer buffer = ByteBuffer.wrap("Hello, NIO!".getBytes());
    channel.write(buffer);
}

// Copy file efficiently with transferTo
try (FileChannel source = FileChannel.open(Path.of("source.txt"), 
            StandardOpenOption.READ);
     FileChannel dest = FileChannel.open(Path.of("dest.txt"), 
            StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
    
    source.transferTo(0, source.size(), dest);
}
```

---

## File Watching

### WatchService (File System Events)

```java
import java.nio.file.*;

// Create watch service
WatchService watchService = FileSystems.getDefault().newWatchService();

// Register directory to watch
Path dir = Path.of("watched-folder");
dir.register(watchService,
    StandardWatchEventKinds.ENTRY_CREATE,
    StandardWatchEventKinds.ENTRY_MODIFY,
    StandardWatchEventKinds.ENTRY_DELETE
);

// Poll for events
System.out.println("Watching " + dir + " for changes...");
while (true) {
    WatchKey key = watchService.take();  // Blocks until event
    
    for (WatchEvent<?> event : key.pollEvents()) {
        WatchEvent.Kind<?> kind = event.kind();
        Path fileName = (Path) event.context();
        
        System.out.println(kind + ": " + fileName);
    }
    
    // Reset key to receive more events
    boolean valid = key.reset();
    if (!valid) break;  // Directory no longer accessible
}
```

### Spring Boot Config File Watching

```java
@Component
public class ConfigFileWatcher {
    
    @PostConstruct
    public void startWatching() {
        new Thread(() -> watchConfigChanges()).start();
    }
    
    private void watchConfigChanges() {
        try (WatchService watchService = FileSystems.getDefault()
                .newWatchService()) {
            
            Path configDir = Path.of("config");
            configDir.register(watchService, 
                StandardWatchEventKinds.ENTRY_MODIFY);
            
            while (true) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    Path changed = (Path) event.context();
                    if (changed.toString().endsWith(".properties")) {
                        reloadConfiguration();
                    }
                }
                key.reset();
            }
        } catch (Exception e) {
            log.error("Config watch failed", e);
        }
    }
}
```

---

## Memory-Mapped Files

### MappedByteBuffer (Large Files)

```java
import java.nio.*;
import java.nio.channels.*;

// Memory-map a file for reading
try (FileChannel channel = FileChannel.open(Path.of("large.dat"), 
        StandardOpenOption.READ)) {
    
    MappedByteBuffer buffer = channel.map(
        FileChannel.MapMode.READ_ONLY,
        0,              // Start position
        channel.size()  // Size to map
    );
    
    // Access file like an array - OS handles paging
    while (buffer.hasRemaining()) {
        byte b = buffer.get();
        // process byte
    }
}

// Memory-map for read-write
try (FileChannel channel = FileChannel.open(Path.of("data.dat"),
        StandardOpenOption.READ, StandardOpenOption.WRITE)) {
    
    MappedByteBuffer buffer = channel.map(
        FileChannel.MapMode.READ_WRITE,
        0,
        1024 * 1024  // 1MB
    );
    
    buffer.putInt(0, 12345);  // Write at position 0
    buffer.force();           // Ensure written to disk
}
```

### When to Use Memory-Mapped Files

| Scenario | Use Memory-Mapped |
|----------|-------------------|
| Files > 1GB | ✅ Yes |
| Random access patterns | ✅ Yes |
| Shared memory between processes | ✅ Yes |
| Small files, sequential read | ❌ No - use Files.readAllBytes |
| Write-heavy workloads | ⚠️ Maybe - test performance |

---

## Best Practices

### ✅ DO

```java
// 1. Use try-with-resources ALWAYS
try (BufferedReader reader = Files.newBufferedReader(path)) {
    // ...
}

// 2. Use Files API for simple operations
String content = Files.readString(path);
Files.writeString(path, content);
Files.copy(source, dest);

// 3. Use streams for large files
try (Stream<String> lines = Files.lines(path)) {
    lines.filter(...).forEach(...);
}

// 4. Specify charset explicitly
Files.readString(path, StandardCharsets.UTF_8);

// 5. Use Path.of() instead of new File()
Path path = Path.of("data", "file.txt");  // Modern
```

### ❌ DON'T

```java
// 1. Don't use File class for new code
File file = new File("data.txt");  // Old way

// 2. Don't forget to close streams
FileInputStream fis = new FileInputStream("data.txt");
// ... might throw, stream never closed!

// 3. Don't load huge files into memory
String huge = Files.readString(path);  // 10GB file!

// 4. Don't ignore character encoding
new FileReader("file.txt");  // Uses system default encoding!

// 5. Don't walk directories without try-with-resources
Files.walk(path).forEach(...)  // Stream never closed!
```

---

## Spring Boot Integration

### Resource Abstraction

```java
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

@Service
public class FileService {
    
    @Value("classpath:data/config.json")
    private Resource configResource;
    
    public String loadConfig() throws IOException {
        // Read from classpath
        try (InputStream is = configResource.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
    
    public void processFile(String path) throws IOException {
        // File system resource
        Resource resource = new FileSystemResource(path);
        
        if (resource.exists() && resource.isReadable()) {
            try (InputStream is = resource.getInputStream()) {
                // process
            }
        }
    }
}
```

### ResourceLoader

```java
@Service
public class DynamicResourceService {
    
    private final ResourceLoader resourceLoader;
    
    public DynamicResourceService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    public String loadResource(String location) throws IOException {
        // Supports: classpath:, file:, http:, etc.
        Resource resource = resourceLoader.getResource(location);
        
        try (InputStream is = resource.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
```

### File Upload

```java
@RestController
@RequestMapping("/api/files")
public class FileUploadController {
    
    @Value("${upload.dir}")
    private String uploadDir;
    
    @PostMapping("/upload")
    public ResponseEntity<String> upload(
            @RequestParam("file") MultipartFile file) throws IOException {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Empty file");
        }
        
        // Save file
        Path destination = Path.of(uploadDir, file.getOriginalFilename());
        Files.copy(file.getInputStream(), destination, 
            StandardCopyOption.REPLACE_EXISTING);
        
        return ResponseEntity.ok("Uploaded: " + destination);
    }
}
```

### File Download

```java
@GetMapping("/download/{filename}")
public ResponseEntity<Resource> download(@PathVariable String filename) 
        throws IOException {
    
    Path file = Path.of(uploadDir, filename);
    Resource resource = new FileSystemResource(file);
    
    if (!resource.exists()) {
        return ResponseEntity.notFound().build();
    }
    
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(HttpHeaders.CONTENT_DISPOSITION, 
            "attachment; filename=\"" + filename + "\"")
        .body(resource);
}
```

---

## Demo: Run the Example

```bash
cd demo-io-nio
mvn compile exec:java -Dexec.mainClass="com.example.IONIODemo"
```

## Key Takeaways

1. **Use java.nio.file.Files** for modern file operations
2. **Use Path.of()** instead of `new File()`
3. **Always use try-with-resources** for I/O operations
4. **Use streams (Files.lines)** for large files
5. **Use NIO channels/buffers** for performance-critical operations
6. **Spring's Resource abstraction** simplifies classpath and file access
