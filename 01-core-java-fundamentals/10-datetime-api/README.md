# Java Date/Time API

> **Master modern date and time handling with java.time**

## 📚 Table of Contents

1. [Introduction](#introduction)
2. [Core Classes](#core-classes)
3. [LocalDate](#localdate)
4. [LocalTime](#localtime)
5. [LocalDateTime](#localdatetime)
6. [ZonedDateTime](#zoneddatetime)
7. [Instant](#instant)
8. [Duration & Period](#duration--period)
9. [Formatting & Parsing](#formatting--parsing)
10. [Time Zone Handling](#time-zone-handling)
11. [Spring Boot Integration](#spring-boot-integration)
12. [Best Practices](#best-practices)

---

## Introduction

### Why java.time?

The old `java.util.Date` and `Calendar` classes had many problems:

| Old API Issues | New API (java.time) |
|----------------|---------------------|
| Mutable (not thread-safe) | Immutable (thread-safe) |
| Months 0-indexed (Jan=0) | Months 1-indexed (Jan=1) |
| Poor API design | Fluent, intuitive API |
| No time zones support | Excellent time zone support |

### Package Overview

```java
java.time.LocalDate        // Date only (2024-03-15)
java.time.LocalTime        // Time only (14:30:00)
java.time.LocalDateTime    // Date + Time (2024-03-15T14:30:00)
java.time.ZonedDateTime    // Date + Time + Zone
java.time.Instant          // Unix timestamp
java.time.Duration         // Time-based amount (hours, minutes, seconds)
java.time.Period           // Date-based amount (years, months, days)
```

---

## Core Classes

### When to Use Which

```
                    ┌──────────────────────────────────────────┐
                    │            What do you need?             │
                    └──────────────────────────────────────────┘
                                        │
            ┌───────────────────────────┼───────────────────────────┐
            ▼                           ▼                           ▼
    Just a date?               Date and time?                 Machine time?
    (birthdays,                (appointments,                 (timestamps,
    holidays)                  meetings)                      logs)
            │                           │                           │
            ▼                           ▼                           ▼
      LocalDate              ┌──────────────────┐              Instant
                             │  Time zone needed? │
                             └──────────────────┘
                                   │       │
                                  No      Yes
                                   │       │
                                   ▼       ▼
                          LocalDateTime  ZonedDateTime
```

---

## LocalDate

Date without time or time zone. Perfect for birthdays, holidays, due dates.

### Creating LocalDate

```java
// Current date
LocalDate today = LocalDate.now();                    // 2024-03-15

// Specific date
LocalDate date = LocalDate.of(2024, 3, 15);           // 2024-03-15
LocalDate date2 = LocalDate.of(2024, Month.MARCH, 15); // More readable

// From string
LocalDate parsed = LocalDate.parse("2024-03-15");     // ISO format

// From epoch day
LocalDate fromEpoch = LocalDate.ofEpochDay(19800);    // Days since 1970-01-01
```

### Accessing Components

```java
LocalDate date = LocalDate.of(2024, 3, 15);

int year = date.getYear();            // 2024
Month month = date.getMonth();        // MARCH
int monthValue = date.getMonthValue(); // 3
int day = date.getDayOfMonth();       // 15
DayOfWeek dow = date.getDayOfWeek();  // FRIDAY
int doy = date.getDayOfYear();        // 75
boolean leap = date.isLeapYear();     // true
int lengthOfMonth = date.lengthOfMonth(); // 31
```

### Manipulating Dates

```java
LocalDate date = LocalDate.of(2024, 3, 15);

// Adding/subtracting (returns new instance - immutable!)
LocalDate tomorrow = date.plusDays(1);        // 2024-03-16
LocalDate nextWeek = date.plusWeeks(1);       // 2024-03-22
LocalDate nextMonth = date.plusMonths(1);     // 2024-04-15
LocalDate nextYear = date.plusYears(1);       // 2025-03-15

LocalDate yesterday = date.minusDays(1);      // 2024-03-14

// With specific values
LocalDate firstOfMonth = date.withDayOfMonth(1);  // 2024-03-01
LocalDate sameInJuly = date.withMonth(7);         // 2024-07-15

// Using TemporalAdjusters
LocalDate nextFriday = date.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
LocalDate lastDayOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());
```

### Comparing Dates

```java
LocalDate date1 = LocalDate.of(2024, 3, 15);
LocalDate date2 = LocalDate.of(2024, 3, 20);

boolean isBefore = date1.isBefore(date2);  // true
boolean isAfter = date1.isAfter(date2);    // false
boolean isEqual = date1.isEqual(date2);    // false

int comparison = date1.compareTo(date2);   // negative
```

---

## LocalTime

Time without date or time zone. Perfect for daily schedules.

### Creating LocalTime

```java
// Current time
LocalTime now = LocalTime.now();                      // 14:30:45.123

// Specific time
LocalTime time = LocalTime.of(14, 30);                // 14:30
LocalTime time2 = LocalTime.of(14, 30, 45);           // 14:30:45
LocalTime time3 = LocalTime.of(14, 30, 45, 123000000); // with nanos

// From string
LocalTime parsed = LocalTime.parse("14:30:45");

// Constants
LocalTime midnight = LocalTime.MIDNIGHT;              // 00:00
LocalTime noon = LocalTime.NOON;                      // 12:00
LocalTime max = LocalTime.MAX;                        // 23:59:59.999999999
```

### Accessing Components

```java
LocalTime time = LocalTime.of(14, 30, 45, 123456789);

int hour = time.getHour();       // 14
int minute = time.getMinute();   // 30
int second = time.getSecond();   // 45
int nano = time.getNano();       // 123456789
```

### Manipulating Time

```java
LocalTime time = LocalTime.of(14, 30);

LocalTime later = time.plusHours(2);       // 16:30
LocalTime earlier = time.minusMinutes(15); // 14:15

LocalTime atNoon = time.withHour(12);      // 12:30
```

---

## LocalDateTime

Combines date and time without time zone.

### Creating LocalDateTime

```java
// Current
LocalDateTime now = LocalDateTime.now();

// Specific
LocalDateTime dt = LocalDateTime.of(2024, 3, 15, 14, 30);
LocalDateTime dt2 = LocalDateTime.of(2024, Month.MARCH, 15, 14, 30, 45);

// Combining
LocalDate date = LocalDate.of(2024, 3, 15);
LocalTime time = LocalTime.of(14, 30);
LocalDateTime combined = LocalDateTime.of(date, time);
LocalDateTime atStartOfDay = date.atStartOfDay();     // 2024-03-15T00:00
LocalDateTime atTime = date.atTime(14, 30);           // 2024-03-15T14:30
```

### Converting Between Types

```java
LocalDateTime dt = LocalDateTime.of(2024, 3, 15, 14, 30);

// Extract date and time
LocalDate date = dt.toLocalDate();    // 2024-03-15
LocalTime time = dt.toLocalTime();    // 14:30

// Add time zone
ZonedDateTime zoned = dt.atZone(ZoneId.of("America/New_York"));
```

---

## ZonedDateTime

Full date/time with time zone. Use for global applications.

### Creating ZonedDateTime

```java
// Current in system zone
ZonedDateTime now = ZonedDateTime.now();

// Current in specific zone
ZonedDateTime nyNow = ZonedDateTime.now(ZoneId.of("America/New_York"));

// Specific date/time in zone
ZonedDateTime zdt = ZonedDateTime.of(2024, 3, 15, 14, 30, 0, 0, 
                                      ZoneId.of("Europe/London"));

// From LocalDateTime
LocalDateTime ldt = LocalDateTime.of(2024, 3, 15, 14, 30);
ZonedDateTime zoned = ldt.atZone(ZoneId.of("Asia/Tokyo"));
```

### Converting Time Zones

```java
ZonedDateTime nyTime = ZonedDateTime.of(2024, 3, 15, 14, 30, 0, 0,
                                         ZoneId.of("America/New_York"));

// Convert to another zone
ZonedDateTime londonTime = nyTime.withZoneSameInstant(ZoneId.of("Europe/London"));
// 14:30 NYC = 18:30 London (during DST)

// Keep same local time, change zone
ZonedDateTime sameLocalInTokyo = nyTime.withZoneSameLocal(ZoneId.of("Asia/Tokyo"));
// 14:30 in Tokyo (different instant!)
```

### Available Zone IDs

```java
// Get all available zones
Set<String> zones = ZoneId.getAvailableZoneIds();
// Includes: America/New_York, Europe/London, Asia/Tokyo, etc.

// Common zones
ZoneId utc = ZoneOffset.UTC;                    // Z or +00:00
ZoneId est = ZoneId.of("America/New_York");     // -05:00 or -04:00 (DST)
ZoneId ist = ZoneId.of("Asia/Kolkata");         // +05:30
```

---

## Instant

Machine-timestamp representing a point on the timeline (epoch seconds).

### Creating Instant

```java
// Current instant
Instant now = Instant.now();

// From epoch seconds
Instant fromEpoch = Instant.ofEpochSecond(1710505800);
Instant fromMilli = Instant.ofEpochMilli(1710505800000L);

// From string
Instant parsed = Instant.parse("2024-03-15T14:30:00Z");
```

### Use Cases

```java
// Measuring elapsed time
Instant start = Instant.now();
// ... do work ...
Instant end = Instant.now();
Duration elapsed = Duration.between(start, end);

// Converting to/from ZonedDateTime
Instant instant = Instant.now();
ZonedDateTime zdt = instant.atZone(ZoneId.of("Europe/London"));
Instant backToInstant = zdt.toInstant();
```

---

## Duration & Period

### Duration (Time-based)

Measures time in hours, minutes, seconds, nanoseconds.

```java
// Creating Duration
Duration twoHours = Duration.ofHours(2);
Duration thirtyMinutes = Duration.ofMinutes(30);
Duration fiveSeconds = Duration.ofSeconds(5);
Duration fromString = Duration.parse("PT2H30M");  // 2 hours 30 minutes

// Between two times
LocalTime start = LocalTime.of(9, 0);
LocalTime end = LocalTime.of(17, 30);
Duration workDay = Duration.between(start, end);  // PT8H30M

// Accessing
long hours = workDay.toHours();         // 8
long minutes = workDay.toMinutes();     // 510
long seconds = workDay.getSeconds();    // 30600

// Arithmetic
Duration doubled = workDay.multipliedBy(2);
Duration half = workDay.dividedBy(2);
```

### Period (Date-based)

Measures time in years, months, days.

```java
// Creating Period
Period oneYear = Period.ofYears(1);
Period twoMonths = Period.ofMonths(2);
Period tenDays = Period.ofDays(10);
Period combined = Period.of(1, 2, 10);  // 1 year, 2 months, 10 days
Period fromString = Period.parse("P1Y2M10D");

// Between two dates
LocalDate start = LocalDate.of(2024, 1, 1);
LocalDate end = LocalDate.of(2024, 3, 15);
Period period = Period.between(start, end);  // P2M14D

// Accessing
int years = period.getYears();   // 0
int months = period.getMonths(); // 2
int days = period.getDays();     // 14

// Using with dates
LocalDate future = LocalDate.now().plus(Period.ofMonths(3));
```

---

## Formatting & Parsing

### DateTimeFormatter

```java
LocalDateTime dt = LocalDateTime.of(2024, 3, 15, 14, 30);

// Predefined formatters
String iso = dt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
// "2024-03-15T14:30:00"

// Custom patterns
DateTimeFormatter custom = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
String formatted = dt.format(custom);  // "15/03/2024 14:30"

// With locale
DateTimeFormatter french = DateTimeFormatter
    .ofPattern("EEEE d MMMM yyyy", Locale.FRENCH);
String frenchDate = dt.format(french);  // "vendredi 15 mars 2024"
```

### Common Patterns

| Pattern | Example | Description |
|---------|---------|-------------|
| `yyyy-MM-dd` | 2024-03-15 | ISO date |
| `HH:mm:ss` | 14:30:45 | 24-hour time |
| `hh:mm a` | 02:30 PM | 12-hour time |
| `dd/MM/yyyy` | 15/03/2024 | European date |
| `MM/dd/yyyy` | 03/15/2024 | US date |
| `EEEE` | Friday | Full day name |
| `MMM` | Mar | Short month |
| `MMMM` | March | Full month |

### Parsing Strings

```java
// Parse with predefined formatter
LocalDate date = LocalDate.parse("2024-03-15");
LocalTime time = LocalTime.parse("14:30:45");
LocalDateTime dt = LocalDateTime.parse("2024-03-15T14:30:45");

// Parse with custom pattern
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
LocalDate parsed = LocalDate.parse("15/03/2024", formatter);

// Handle parsing errors
try {
    LocalDate bad = LocalDate.parse("invalid");
} catch (DateTimeParseException e) {
    System.out.println("Cannot parse date");
}
```

---

## Time Zone Handling

### Daylight Saving Time

```java
// DST transition example (US 2024)
ZonedDateTime beforeDST = ZonedDateTime.of(2024, 3, 10, 1, 30, 0, 0,
                                            ZoneId.of("America/New_York"));
// 2024-03-10T01:30-05:00[America/New_York]

ZonedDateTime afterDST = beforeDST.plusHours(1);
// 2024-03-10T03:30-04:00[America/New_York]
// Jumped from 2:00 to 3:00!

// Check for overlap/gap
ZoneRules rules = ZoneId.of("America/New_York").getRules();
boolean isDST = rules.isDaylightSavings(Instant.now());
```

### Best Practices for Time Zones

```java
// Store in UTC
Instant utcTimestamp = Instant.now();

// Convert to user's time zone for display
ZoneId userZone = ZoneId.of("America/New_York");
ZonedDateTime userTime = utcTimestamp.atZone(userZone);

// When scheduling across zones
ZonedDateTime meeting = ZonedDateTime.of(2024, 3, 15, 14, 0, 0, 0,
                                          ZoneId.of("America/New_York"));
ZonedDateTime inLondon = meeting.withZoneSameInstant(ZoneId.of("Europe/London"));
ZonedDateTime inTokyo = meeting.withZoneSameInstant(ZoneId.of("Asia/Tokyo"));
```

---

## Spring Boot Integration

### Entity Field Types

```java
@Entity
public class Event {
    @Id
    private Long id;
    
    private LocalDate date;           // Just date
    private LocalTime time;           // Just time
    private LocalDateTime createdAt;  // Date + Time
    private Instant timestamp;        // UTC timestamp
}
```

### REST API Serialization

```java
@RestController
public class EventController {
    
    @GetMapping("/events/{id}")
    public Event getEvent(@PathVariable Long id) {
        // LocalDate/LocalDateTime serialized as ISO strings
        return eventService.findById(id);
    }
}

// JSON output:
// {
//   "date": "2024-03-15",
//   "time": "14:30:00",
//   "createdAt": "2024-03-15T14:30:00"
// }
```

### Custom JSON Format

```java
public class Event {
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;
}
```

### JPA Converter (if needed for legacy)

```java
@Converter(autoApply = true)
public class LocalDateAttributeConverter 
    implements AttributeConverter<LocalDate, Date> {
    
    @Override
    public Date convertToDatabaseColumn(LocalDate localDate) {
        return localDate == null ? null : Date.valueOf(localDate);
    }
    
    @Override
    public LocalDate convertToEntityAttribute(Date sqlDate) {
        return sqlDate == null ? null : sqlDate.toLocalDate();
    }
}
```

---

## Best Practices

### ✅ DO

```java
// 1. Use appropriate type for the use case
LocalDate birthday;           // No time component
LocalDateTime appointment;    // Local time
Instant createdAt;           // UTC timestamp
ZonedDateTime meeting;       // Time zone matters

// 2. Store in UTC, display in local
Instant stored = Instant.now();
ZonedDateTime displayed = stored.atZone(userTimeZone);

// 3. Use ISO format for APIs
// 2024-03-15T14:30:00Z

// 4. Be explicit about time zones
ZonedDateTime.now(ZoneId.of("UTC"));
```

### ❌ DON'T

```java
// 1. Don't use old Date/Calendar
java.util.Date oldDate;  // ❌
LocalDateTime newDate;   // ✅

// 2. Don't ignore time zones in global apps
LocalDateTime.now();     // ❌ Which zone?
Instant.now();          // ✅ UTC

// 3. Don't use string manipulation for dates
"2024-03-15".substring(5, 7);  // ❌
```

---

## Demo: Run the Example

```bash
cd demo-datetime-api
mvn compile exec:java -Dexec.mainClass="com.example.DateTimeDemo"
```

## Key Takeaways

1. **Use java.time** - Never use Date/Calendar
2. **Choose the right type**: LocalDate, LocalTime, LocalDateTime, ZonedDateTime, Instant
3. **Store in UTC** - Convert to local time for display
4. **Use DateTimeFormatter** for parsing/formatting
5. **Handle DST** - Be aware of daylight saving transitions
