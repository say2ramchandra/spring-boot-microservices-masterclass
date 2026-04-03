# Web BFF (Backend for Frontend)

> **Optimized API gateway specifically designed for web applications**

## 📚 Overview

The Web BFF provides a tailored API interface for desktop web applications, offering rich data structures, detailed responses, and features optimized for large-screen experiences. It aggregates data from multiple backend services and formats responses with complete information for web-based UIs.

---

## 🎯 Purpose

### Why Web BFF?

**Web-Specific Advantages:**
- High bandwidth connections
- Large screen real estate
- Desktop processing power
- Rich interactions (hover, drag-drop)
- Multiple panels/windows

**Web BFF Solutions:**
- Detailed JSON responses (complete data)
- Nested objects (reduce join logic in frontend)
- Multiple image sizes (responsive images)
- Rich metadata (tooltips, descriptions)
- Advanced filtering and sorting

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────┐
│          Web Application                    │
│   (React/Angular/Vue/Desktop Browser)       │
└───────────────┬─────────────────────────────┘
                │ RESTful API
                ▼
┌─────────────────────────────────────────────┐
│           Web BFF (Port 8082)               │
│  - Aggregates backend data                  │
│  - Provides rich responses                  │
│  - Includes nested objects                  │
│  - Handles complex queries                  │
└───────────────┬─────────────────────────────┘
                │
    ┌───────────┼───────────┐
    ▼           ▼           ▼
┌────────┐  ┌────────┐  ┌────────┐
│Product │  │ User   │  │Order   │
│Service │  │Service │  │Service │
└────────┘  └────────┘  └────────┘
```

---

## 🚀 Getting Started

### Prerequisites

```bash
# Java 17
java -version

# Maven
mvn -version

# Shared service must be running on port 8083
cd ../shared-service
mvn spring-boot:run
```

### Run Web BFF

```bash
# Navigate to web-bff directory
cd web-bff

# Build and run
mvn clean spring-boot:run

# Server starts on: http://localhost:8082
```

---

## 📡 API Endpoints

### 1. Web Dashboard (Rich Aggregated Data)

**GET** `/api/web/dashboard`

**Purpose:** Complete dashboard data with nested objects

**Response:**
```json
{
  "featuredProducts": [
    {
      "id": 1,
      "name": "MacBook Pro 16\"",
      "description": "Apple MacBook Pro 16-inch with M3 Pro chip",
      "price": 2499.00,
      "category": {
        "name": "Electronics",
        "description": "Electronic devices and gadgets"
      },
      "images": {
        "thumbnail": "/images/thumb_macbook.jpg",
        "medium": "/images/medium_macbook.jpg",
        "large": "/images/large_macbook.jpg"
      },
      "stock": 25,
      "available": true,
      "rating": 4.8,
      "reviewCount": 342
    }
  ],
  "categories": [
    {
      "name": "Electronics",
      "description": "Electronic devices",
      "productCount": 4,
      "icon": "electronics.svg"
    }
  ],
  "statistics": {
    "totalProducts": 8,
    "totalCategories": 3,
    "newArrivals": 3,
    "topSellingCategory": "Electronics"
  },
  "metadata": {
    "lastUpdated": "2026-02-16T10:30:00Z",
    "cacheExpiry": 60
  }
}
```

**Features:**
- ✅ Complete nested objects
- ✅ Multiple image sizes
- ✅ Rich metadata
- ✅ Detailed statistics

**Usage:**
```bash
curl http://localhost:8082/api/web/dashboard | jq
```

---

### 2. Product List (Web Detailed)

**GET** `/api/web/products`

**Query Parameters:**
- `category` - Filter by category
- `minPrice` - Minimum price
- `maxPrice` - Maximum price
- `sort` - Sort field (name, price, rating)
- `order` - Sort order (asc, desc)
- `page` - Page number (default: 0)
- `size` - Items per page (default: 20)

**Response:**
```json
{
  "products": [
    {
      "id": 1,
      "name": "MacBook Pro 16\"",
      "description": "Apple MacBook Pro 16-inch with M3 Pro chip, 36GB RAM, 512GB SSD",
      "price": 2499.00,
      "category": {
        "name": "Electronics",
        "description": "Electronic devices and gadgets",
        "slug": "electronics"
      },
      "images": {
        "thumbnail": "/images/thumb_macbook.jpg",
        "medium": "/images/medium_macbook.jpg",
        "large": "/images/large_macbook.jpg",
        "xlarge": "/images/xlarge_macbook.jpg"
      },
      "stock": 25,
      "available": true,
      "rating": 4.8,
      "reviewCount": 342,
      "tags": ["laptop", "apple", "professional"],
      "specifications": {
        "processor": "Apple M3 Pro",
        "ram": "36GB",
        "storage": "512GB SSD"
      },
      "createdAt": "2024-01-15T10:00:00Z",
      "updatedAt": "2026-02-15T14:30:00Z"
    }
  ],
  "pagination": {
    "currentPage": 0,
    "totalPages": 1,
    "totalItems": 8,
    "itemsPerPage": 20,
    "hasNext": false,
    "hasPrevious": false
  },
  "filters": {
    "appliedFilters": {
      "category": "Electronics"
    },
    "availableFilters": {
      "categories": ["Electronics", "Books", "Accessories"],
      "priceRanges": ["0-50", "50-100", "100-500", "500+"]
    }
  }
}
```

**Features:**
- ✅ Full product details
- ✅ Nested category objects
- ✅ Multiple image sizes
- ✅ Rich pagination metadata
- ✅ Available filters

**Usage:**
```bash
# All products
curl http://localhost:8082/api/web/products | jq

# Filter by category
curl http://localhost:8082/api/web/products?category=Electronics | jq

# Price range and sorting
curl "http://localhost:8082/api/web/products?minPrice=50&maxPrice=500&sort=price&order=asc" | jq

# Pagination
curl "http://localhost:8082/api/web/products?page=0&size=10" | jq
```

---

### 3. Product Details (Complete View)

**GET** `/api/web/products/{id}`

**Response:**
```json
{
  "id": 1,
  "name": "MacBook Pro 16\"",
  "description": "Apple MacBook Pro 16-inch with M3 Pro chip",
  "longDescription": "The MacBook Pro 16-inch delivers exceptional performance...",
  "price": 2499.00,
  "comparePrice": 2799.00,
  "discount": 10.7,
  "category": {
    "id": 1,
    "name": "Electronics",
    "description": "Electronic devices and gadgets",
    "slug": "electronics",
    "parentCategory": null
  },
  "images": {
    "thumbnail": "/images/thumb_macbook.jpg",
    "medium": "/images/medium_macbook.jpg",
    "large": "/images/large_macbook.jpg",
    "xlarge": "/images/xlarge_macbook.jpg",
    "gallery": [
      "/images/macbook_1.jpg",
      "/images/macbook_2.jpg",
      "/images/macbook_3.jpg"
    ]
  },
  "stock": 25,
  "available": true,
  "rating": 4.8,
  "reviewCount": 342,
  "tags": ["laptop", "apple", "professional", "m3"],
  "specifications": {
    "processor": "Apple M3 Pro (12-core CPU, 18-core GPU)",
    "ram": "36GB Unified Memory",
    "storage": "512GB SSD",
    "display": "16.2-inch Liquid Retina XDR",
    "battery": "Up to 22 hours",
    "weight": "2.15 kg"
  },
  "relatedProducts": [
    {
      "id": 2,
      "name": "Dell UltraSharp Monitor",
      "price": 599.99,
      "imageUrl": "/images/thumb_dell_monitor.jpg"
    },
    {
      "id": 4,
      "name": "Mechanical Keyboard",
      "price": 149.99,
      "imageUrl": "/images/thumb_keyboard.jpg"
    }
  ],
  "reviews": {
    "summary": {
      "average": 4.8,
      "total": 342,
      "distribution": {
        "5": 280,
        "4": 45,
        "3": 10,
        "2": 5,
        "1": 2
      }
    },
    "recent": [
      {
        "id": 1,
        "author": "John Doe",
        "rating": 5,
        "comment": "Excellent laptop for development!",
        "date": "2026-02-10T14:30:00Z"
      }
    ]
  },
  "metadata": {
    "createdAt": "2024-01-15T10:00:00Z",
    "updatedAt": "2026-02-15T14:30:00Z",
    "views": 15420,
    "favorites": 892
  }
}
```

**Features:**
- ✅ Complete product information
- ✅ Full specifications
- ✅ Image gallery
- ✅ Related products (full objects)
- ✅ Review summary
- ✅ Metadata and analytics

**Usage:**
```bash
curl http://localhost:8082/api/web/products/1 | jq
```

---

### 4. Advanced Search

**GET** `/api/web/search`

**Query Parameters:**
- `q` - Search query
- `category` - Filter by category
- `minPrice` - Minimum price
- `maxPrice` - Maximum price
- `inStock` - Only in-stock items (true/false)
- `sort` - Sort field
- `page` - Page number

**Response:**
```json
{
  "query": "laptop",
  "results": [
    {
      "id": 1,
      "name": "MacBook Pro 16\"",
      "description": "Apple MacBook Pro...",
      "price": 2499.00,
      "category": { "name": "Electronics" },
      "images": { "thumbnail": "...", "medium": "..." },
      "available": true,
      "relevanceScore": 0.95,
      "highlights": {
        "name": "Mac<em>Book</em> Pro",
        "description": "Professional <em>laptop</em>..."
      }
    }
  ],
  "pagination": {
    "total": 2,
    "page": 0,
    "size": 20
  },
  "facets": {
    "categories": {
      "Electronics": 2
    },
    "priceRanges": {
      "1000-3000": 2
    }
  },
  "suggestions": ["laptop stand", "laptop bag"]
}
```

**Usage:**
```bash
curl "http://localhost:8082/api/web/search?q=laptop&sort=relevance" | jq
```

---

### 5. Categories (Detailed)

**GET** `/api/web/categories`

**Response:**
```json
{
  "categories": [
    {
      "id": 1,
      "name": "Electronics",
      "description": "Electronic devices and gadgets",
      "slug": "electronics",
      "icon": "electronics.svg",
      "productCount": 4,
      "subcategories": [
        {
          "name": "Laptops",
          "productCount": 2
        },
        {
          "name": "Monitors",
          "productCount": 1
        }
      ],
      "featuredProducts": [
        {
          "id": 1,
          "name": "MacBook Pro",
          "price": 2499.00
        }
      ]
    }
  ],
  "metadata": {
    "total": 3,
    "lastUpdated": "2026-02-16T10:30:00Z"
  }
}
```

**Usage:**
```bash
curl http://localhost:8082/api/web/categories | jq
```

---

## ⚙️ Configuration

**application.yml**

```yaml
server:
  port: 8082

spring:
  application:
    name: web-bff

# Backend service URLs
backend:
  service:
    url: http://localhost:8083

# Web-specific settings
web:
  cache:
    ttl: 60  # 1 minute
  pagination:
    default-size: 20
    max-size: 100
  images:
    include-all-sizes: true
  response:
    include-metadata: true
```

---

## 🎯 Web Optimization Strategies

### 1. Rich Data Structures

```java
// Complete nested objects
{
  "product": {
    "id": 1,
    "name": "Laptop",
    "category": {
      "id": 1,
      "name": "Electronics",
      "description": "...",
      "slug": "electronics"
    },
    "images": {
      "thumbnail": "...",
      "medium": "...",
      "large": "...",
      "xlarge": "..."
    }
  }
}
```

### 2. Multiple Image Sizes

```java
// Responsive images for web
"images": {
  "thumbnail": "thumb_100x100.jpg",   // Lists
  "medium": "medium_400x400.jpg",      // Cards
  "large": "large_800x800.jpg",        // Details
  "xlarge": "xlarge_1200x1200.jpg"     // Lightbox
}
```

### 3. Advanced Pagination

```java
{
  "pagination": {
    "currentPage": 2,
    "totalPages": 10,
    "totalItems": 195,
    "itemsPerPage": 20,
    "hasNext": true,
    "hasPrevious": true,
    "nextPage": 3,
    "previousPage": 1
  }
}
```

### 4. Rich Metadata

```java
{
  "metadata": {
    "createdAt": "2024-01-15T10:00:00Z",
    "updatedAt": "2026-02-15T14:30:00Z",
    "version": 3,
    "author": "admin",
    "views": 15420,
    "favorites": 892
  }
}
```

---

## 🔍 Key Differences from Mobile BFF

| Feature | Web BFF | Mobile BFF |
|---------|---------|------------|
| **Response Size** | Rich (5-10KB) | Minimal (1-2KB) |
| **Field Names** | Descriptive | Abbreviated |
| **Images** | All sizes | Thumbnails only |
| **Data Depth** | Deep (nested objects) | Shallow (IDs only) |
| **Pagination** | Page numbers | Infinite scroll |
| **Metadata** | Extensive | Minimal |
| **Filter Options** | Advanced | Basic |
| **Error Details** | Verbose | Simple |

---

## 🧪 Testing

### Manual Testing

```bash
# Start shared service first
cd ../shared-service && mvn spring-boot:run &

# Wait for shared service to start
sleep 30

# Start web BFF
cd ../web-bff && mvn spring-boot:run &

# Test dashboard
curl http://localhost:8082/api/web/dashboard | jq

# Test products list with filters
curl "http://localhost:8082/api/web/products?category=Electronics&sort=price&order=asc" | jq

# Test product details
curl http://localhost:8082/api/web/products/1 | jq

# Test search
curl "http://localhost:8082/api/web/search?q=laptop" | jq

# Test categories
curl http://localhost:8082/api/web/categories | jq
```

### Compare with Mobile BFF

```bash
# Compare response sizes
echo "Mobile BFF:"
curl -s http://localhost:8081/api/mobile/products | wc -c

echo "Web BFF:"
curl -s http://localhost:8082/api/web/products | wc -c

# Web BFF typically 3-5x larger but more complete
```

---

## 📊 Performance Metrics

### Target Metrics

- **Response Time:** < 300ms (p95)
- **Payload Size:** < 10KB (product list)
- **Cache Hit Rate:** > 70%
- **Nested Object Depth:** Max 3 levels

### Monitoring

```bash
# Response times
curl -w "@curl-format.txt" -o /dev/null -s http://localhost:8082/api/web/dashboard

# Payload size
curl -s http://localhost:8082/api/web/products | wc -c
```

---

## 🔄 Typical Web App Flow

### 1. Page Load

```
Web App → GET /api/web/dashboard
       ← { featuredProducts, categories, statistics }
```

### 2. Browse Products (Paginated)

```
Web App → GET /api/web/products?page=0&size=20
       ← { products: [...], pagination: {...} }
       
       (User clicks page 2)
       
Web App → GET /api/web/products?page=1&size=20
       ← { products: [...], pagination: {...} }
```

### 3. View Product Details

```
Web App → GET /api/web/products/1
       ← { product: {...}, relatedProducts: [...], reviews: {...} }
```

### 4. Advanced Search

```
Web App → GET /api/web/search?q=laptop&category=Electronics&minPrice=1000
       ← { results: [...], facets: {...}, suggestions: [...] }
```

---

## 🐛 Troubleshooting

### Issue: Large Response Sizes

```bash
# Check if all image sizes are needed
# Consider reducing response depth
# Implement field selection (sparse fieldsets)
```

### Issue: Slow Aggregation

```bash
# Check backend service response times
curl -w "%{time_total}\n" -o /dev/null -s http://localhost:8083/api/products

# Consider implementing caching
# Consider parallel backend calls
```

---

## 🎓 Key Takeaways

1. ✅ **Web BFF optimizes for rich UX** - Complete data structures
2. ✅ **Nested objects reduce frontend complexity** - Less data manipulation
3. ✅ **Multiple image sizes** - Responsive design support
4. ✅ **Advanced filtering and pagination** - Desktop-class features
5. ✅ **Rich metadata** - Analytics and enhanced UX
6. ✅ **Detailed error messages** - Better debugging

---

## 🔗 Related Services

- **Mobile BFF** - [../mobile-bff/](../mobile-bff/) - Mobile-optimized API
- **Shared Service** - [../shared-service/](../shared-service/) - Backend data provider

---

**Web BFF provides 3x more data than Mobile BFF for richer desktop experiences!** 🖥️✨
