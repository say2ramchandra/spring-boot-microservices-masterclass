# Template Method Pattern Demo - Data Export & Order Processing

> **Demonstrates Template Method Pattern with two practical examples**

## 🎯 What is Template Method Pattern?

**Problem:** You have an algorithm with invariant steps, but some steps need different implementations.

**Solution:** Define the skeleton of an algorithm in a base class, deferring variant steps to subclasses.

---

## 🏗️ How It Works

### Without Template Method (Bad):
```java
public class CsvExporter {
    public void export(Data data) {
        validate(data);          // Same
        prepareHeader();         // Different
        formatRows();            // Different
        generateFooter();        // Different
        writeOutput();           // Same
    }
}

public class PdfExporter {
    public void export(Data data) {
        validate(data);          // Same (duplicated!)
        prepareHeader();         // Different
        formatRows();            // Different
        generateFooter();        // Different
        writeOutput();           // Same (duplicated!)
    }
}
// 😱 Common steps duplicated across classes!
```

### With Template Method (Good):
```java
public abstract class DataExportTemplate {
    // Template method - defines the algorithm skeleton
    public final void export(Data data) {
        validate(data);       // Common (implemented here)
        prepareHeader();      // Abstract (subclass implements)
        formatRows();         // Abstract (subclass implements)
        generateFooter();     // Hook (optional override)
        writeOutput();        // Common (implemented here)
    }
    
    protected abstract void prepareHeader();
    protected abstract void formatRows();
}
// ✅ Common code in one place, variations in subclasses!
```

---

## 🚀 Running the Demo

### Prerequisites
- Java 17+
- Maven 3.8+

### Start the Application
```bash
cd 11-advanced-patterns/01-design-patterns/demo-template-method-pattern
mvn spring-boot:run
```

Application runs on: **http://localhost:8095**

---

## 🧪 Example 1: Data Export

### Export as CSV
```bash
curl http://localhost:8095/api/export/sample/csv
```

### Export as PDF
```bash
curl http://localhost:8095/api/export/sample/pdf
```

### Export as Excel
```bash
curl http://localhost:8095/api/export/sample/excel
```

### Export Custom Data
```bash
curl -X POST http://localhost:8095/api/export/csv \
  -H "Content-Type: application/json" \
  -d '[
    {"id": 1, "product": "Laptop", "price": 999.99},
    {"id": 2, "product": "Mouse", "price": 29.99}
  ]'
```

**Response (CSV):**
```json
{
  "success": true,
  "filename": "export.csv",
  "exportType": "CSV",
  "content": "id,product,price\n1,Laptop,999.99\n2,Mouse,29.99\n# Total records: 2",
  "executionSteps": [
    "1. Validating data",
    "2. Initializing export",
    "3. Preparing header",
    "4. Formatting 2 data rows",
    "5. Generating footer",
    "6. Compiling final output",
    "7. Cleanup",
    "✅ Export completed in 5ms"
  ],
  "durationMs": 5
}
```

---

## 🧪 Example 2: Order Processing

### Process Standard Order (5-7 days)
```bash
curl http://localhost:8095/api/orders/sample/standard
```

### Process Express Order (1-2 days)
```bash
curl http://localhost:8095/api/orders/sample/express
```

### Process International Order (7-21 days)
```bash
curl http://localhost:8095/api/orders/sample/international
```

**Response (Express):**
```json
{
  "success": true,
  "orderId": "ORD-A1B2C3D4",
  "orderType": "Express",
  "subtotal": 119.97,
  "shippingCost": 20.99,
  "discount": 6.00,
  "total": 134.96,
  "trackingNumber": "EXP-X9Y8Z7W6",
  "processingSteps": [
    "1. Validating order",
    "2. Reserving inventory",
    "3. Calculating shipping",
    "4. Applying discounts",
    "5. Calculating total",
    "6. Processing payment",
    "7. Generating shipping label",
    "8. Sending notifications",
    "9. Post-processing",
    "✅ Order processed in 12ms"
  ]
}
```

---

## 📚 Pattern Components

### Template Class Structure
```
┌────────────────────────────────────────────┐
│         DataExportTemplate (Abstract)      │
├────────────────────────────────────────────┤
│ + export() : final       ← Template Method │
│ # validateData()         ← Common (final)  │
│ # initialize()           ← Hook (optional) │
│ # prepareHeader()        ← Abstract        │
│ # formatDataRows()       ← Abstract        │
│ # generateFooter()       ← Hook (optional) │
│ # compileOutput()        ← Abstract        │
│ # cleanup()              ← Hook (optional) │
└─────────────────┬──────────────────────────┘
                  │
    ┌─────────────┼─────────────┐
    ▼             ▼             ▼
┌────────┐  ┌─────────┐  ┌───────────┐
│ CsvExp │  │ PdfExp  │  │ ExcelExp  │
│ orter  │  │ orter   │  │ orter     │
└────────┘  └─────────┘  └───────────┘
```

### Method Types

| Type | Description | Override |
|------|-------------|----------|
| **Template Method** | Defines algorithm skeleton | Never (final) |
| **Abstract Method** | Must be implemented by subclass | Required |
| **Hook Method** | Has default implementation | Optional |
| **Common Method** | Shared implementation | Rarely |

---

## 🎯 When to Use Template Method

### ✅ Good Use Cases:
- **Data export/import** (CSV, PDF, Excel, JSON)
- **Order processing** (Standard, Express, International)
- **Report generation** (Daily, Weekly, Monthly)
- **Authentication flows** (OAuth, SAML, Basic)
- **Payment processing** (Credit Card, PayPal, Crypto)
- **Document rendering** (HTML, PDF, Word)

### ❌ Avoid When:
- Algorithm varies completely between implementations
- No common steps exist
- Subclasses need different algorithm order
- Composition would be simpler

---

## 💡 Test Your Knowledge

1. **Why is the template method declared as `final`?**
   - A) For performance
   - B) To prevent subclasses from changing the algorithm structure
   - C) Because abstract classes require final methods
   - D) To make it thread-safe

2. **What is a "hook" method in Template Method pattern?**
   - A) A method that must be abstract
   - B) A method with default implementation that subclasses can optionally override
   - C) A private method
   - D) A static method

3. **What's the difference between abstract and hook methods?**
   - A) None, they're the same
   - B) Abstract must be overridden; hooks have defaults
   - C) Hooks must be overridden; abstract have defaults
   - D) Abstract is public; hooks are private

4. **Which step is common across all exporters?**
   - A) Format data rows
   - B) Prepare header
   - C) Validate data
   - D) Generate footer

5. **Why use Template Method instead of Strategy pattern?**
   - A) When you want to vary the entire algorithm
   - B) When you want to vary specific steps but keep the algorithm skeleton
   - C) When you need runtime algorithm switching
   - D) When inheritance should be avoided

<details>
<summary>📝 Answers</summary>

1. **B** - To prevent subclasses from changing the algorithm structure
2. **B** - Hook methods have default implementation, subclasses can optionally override
3. **B** - Abstract methods must be overridden; hooks have default implementations
4. **C** - `validateData()` is common implementation in base class
5. **B** - Template Method varies specific steps while keeping the skeleton fixed

</details>

---

## 📚 Further Reading

- [Template Method - Refactoring Guru](https://refactoring.guru/design-patterns/template-method)
- [Template Method vs Strategy](https://stackoverflow.com/questions/669271/)
- [Spring's JdbcTemplate - Real World Example](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/JdbcTemplate.html)

---

**Happy Learning!** 📋✨
