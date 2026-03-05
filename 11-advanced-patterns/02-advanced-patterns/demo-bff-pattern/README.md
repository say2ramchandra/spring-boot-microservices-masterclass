# Backend for Frontend (BFF) Pattern Demo

> **Demonstrates BFF pattern with separate backends for Mobile and Web clients**

## 🎯 What is BFF Pattern?

**Problem:** Different client types (web, mobile, desktop) need different data formats, requiring complex API logic in a single backend.

**Solution:** Create separate backend services for each client type, each optimized for that specific frontend.

---

## 📦 Services Overview

This demo consists of **3 microservices**:

| Service | Port | Purpose | Audience |
|---------|------|---------|----------|
| **Shared Service** | 8100 | Backend with product data | Internal only |
| **Web BFF** | 8101 | Optimized for web browsers | Desktop users |
| **Mobile BFF** | 8102 | Optimized for mobile apps | iOS/Android |

---

## 🚀 Running the Demo

### Step 1: Start Shared Service (Port 8100)
```bash
cd 11-advanced-patterns/02-advanced-patterns/demo-bff-pattern/shared-service
mvn spring-boot:run
```

### Step 2: Start Web BFF (Port 8101)
```bash
cd 11-advanced-patterns/02-advanced-patterns/demo-bff-pattern/web-bff
mvn spring-boot:run
```

### Step 3: Start Mobile BFF (Port 8102)
```bash
cd 11-advanced-patterns/02-advanced-patterns/demo-bff-pattern/mobile-bff
mvn spring-boot:run
```

✅ All services should be running on ports **8100, 8101, 8102**

---

## 🧪 Testing the Pattern

### 1. Shared Service (Internal Backend)
```bash
# Get all products (raw data)
curl http://localhost:8100/api/products

# Get single product
curl http://localhost:8100/api/products/1
```

**Response (Raw):**
```json
{
  "id": 1,
  "name": "Gaming Laptop",
  "shortDescription": "High-performance gaming laptop",
  "fullDescription": "Experience ultimate gaming with RTX 4090...",
  "price": 2499.99,
  "category": "Electronics",
  "stock": 15,
  "imageUrl": "https://example.com/laptop-large.jpg",
  "thumbnailUrl": "https://example.com/laptop-thumb.jpg",
  "rating": 4.8,
  "reviewCount": 156,
  "brand": "ASUS ROG",
  "sku": "LAP-001"
}
```

---

### 2. Web BFF (Desktop Optimized)
```bash
# Get products for web browsers
curl http://localhost:8101/api/web/products

# Get single product for web
curl http://localhost:8101/api/web/products/1
```

**Response (Web Optimized):**
```json
{
  "id": 1,
  "name": "Gaming Laptop",
  "fullDescription": "Experience ultimate gaming with RTX 4090, Intel i9-13900K...",
  "formattedPrice": "$2,499.99",
  "priceWithCurrency": "2,499.99 USD",
  "rawPrice": 2499.99,
  "category": "Electronics",
  "inStock": true,
  "stockMessage": "In Stock (15 available)",
  "imageUrl": "https://example.com/laptop-large.jpg",
  "thumbnailUrl": "https://example.com/laptop-thumb.jpg",
  "rating": 4.8,
  "reviewCount": 156,
  "ratingDisplay": "4.8/5.0 (156 reviews)",
  "brand": "ASUS ROG",
  "sku": "LAP-001",
  "isFavorite": false,
  "cartQuantity": 0,
  "deliveryEstimate": "✅ Fast shipping - Delivery in 1-2 business days"
}
```

**Web Enhancements:**
- ✅ Formatted prices with currency symbols
- ✅ Rich stock messages
- ✅ Detailed rating display
- ✅ Delivery estimates
- ✅ Wishlist/cart integration placeholders
- ✅ Large images for desktop screens

---

### 3. Mobile BFF (Mobile Optimized)
```bash
# Get lightweight product list for mobile
curl http://localhost:8102/api/mobile/products

# Get detailed product for mobile
curl http://localhost:8102/api/mobile/products/1
```

**Response (Mobile List - Lightweight):**
```json
{
  "id": 1,
  "name": "Gaming Laptop",
  "shortDesc": "High-performance gaming laptop",
  "price": 2499.99,
  "currency": "USD",
  "inStock": true,
  "thumbnailUrl": "https://example.com/laptop-thumb.jpg",
  "rating": 4.8,
  "brand": "ASUS ROG",
  "isNew": true,
  "badge": "⭐ TOP RATED",
  "discount": 0
}
```

**Response (Mobile Detail - Full):**
```json
{
  "id": 1,
  "name": "Gaming Laptop",
  "shortDesc": "High-performance gaming laptop",
  "fullDesc": "Experience ultimate gaming with RTX 4090, Intel i9-13900K...",
  "price": 2499.99,
  "formattedPrice": "$2499.99",
  "currency": "USD",
  "inStock": true,
  "stock": 15,
  "thumbnailUrl": "https://example.com/laptop-thumb.jpg",
  "imageUrl": "https://example.com/laptop-large.jpg",
  "rating": 4.8,
  "reviewCount": 156,
  "brand": "ASUS ROG",
  "sku": "LAP-001",
  "isNew": true,
  "badge": "⭐ TOP RATED",
  "deliveryTime": "1-2 days",
  "freeShipping": true
}
```

**Mobile Enhancements:**
- ✅ Minimal payload (saves bandwidth)
- ✅ Small thumbnails only in list view
- ✅ Visual badges (NEW, TOP RATED, LIMITED)
- ✅ Quick delivery info
- ✅ Free shipping indicator
- ✅ Truncated descriptions in list

---

## 📊 Data Comparison

### Same Product, Different Responses:

| Field | Shared Service | Web BFF | Mobile BFF (List) |
|-------|----------------|---------|-------------------|
| **Payload Size** | ~800 bytes | ~1200 bytes | ~300 bytes |
| **Description** | Both short & full | Full only | Short only (truncated) |
| **Price Format** | 2499.99 | "$2,499.99" + "2,499.99 USD" | 2499.99 + "USD" |
| **Stock Info** | 15 | "In Stock (15 available)" | true/false |
| **Images** | Both URLs | Both URLs | Thumbnail only |
| **Rating** | 4.8 | "4.8/5.0 (156 reviews)" | 4.8 |
| **Delivery** | - | "Fast shipping - 1-2 days" | "1-2 days" |
| **Badges** | - | - | "⭐ TOP RATED" |
| **Wishlist** | - | Placeholder | - |

---

## 🏗️ Architecture

```
┌─────────────────┐
│  Web Browser    │
│  (Desktop)      │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   Web BFF       │ ◄─── Formats prices, adds delivery info
│   Port 8101     │      Uses large images
└────────┬────────┘      Adds wishlist/cart data
         │
         │
         ▼
┌─────────────────┐      ┌─────────────────┐
│ Shared Service  │◄─────│  Mobile BFF     │
│   Port 8100     │      │   Port 8102     │
│                 │      └────────▲────────┘
│ - Products DB   │               │
│ - Raw data      │               │
└─────────────────┘      ┌────────┴────────┐
                         │  Mobile App     │
                         │  (iOS/Android)  │
                         └─────────────────┘
                         
                         Mobile BFF features:
                         - Minimal payload
                         - Small thumbnails
                         - Visual badges
                         - Truncated text
```

---

## ✨ Benefits of BFF Pattern

### 1. **Client-Specific Optimization**
- **Web:** Rich, formatted data for large screens
- **Mobile:** Lightweight payloads to save data/battery

### 2. **Independent Evolution**
- Change mobile API without touching web
- Deploy updates independently
- Different release cycles

### 3. **Performance**
- Reduce over-fetching (mobile gets only what it needs)
- Reduce under-fetching (web gets all details at once)
- Optimize image sizes per platform

### 4. **Security**
- Different authentication strategies
- Rate limiting per client type
- Client-specific permissions

### 5. **Team Autonomy**
- Mobile team owns mobile BFF
- Web team owns web BFF
- Backend team owns shared service

---

## 🎯 Real-World Use Cases

1. **Netflix** - TV, mobile, web have different UIs and data needs
2. **Spotify** - Desktop vs mobile playback features
3. **Amazon** - Different product displays per platform
4. **Uber** - Rider app vs Driver app vs Admin web
5. **Banking Apps** - Mobile (touch ID) vs Web (full features)

---

## 💡 When to Use BFF Pattern

### ✅ Use When:
- Multiple client platforms (web, iOS, Android, TV, IoT)
- Different data requirements per client
- Need platform-specific optimizations
- Large teams with platform specialization
- Want independent deployment cycles

### ❌ Don't Use When:
- Only one client type
- All clients need identical data
- Small team (overhead not justified)
- Simple CRUD application
- Tight coupling is acceptable

---

## 🔍 Pattern Variations

### 1. **Classic BFF** (This Demo)
One BFF per client type.

### 2. **GraphQL BFF**
Client queries exactly what it needs.

### 3. **API Gateway + BFFs**
Gateway handles auth, BFFs handle transformation.

### 4. **Micro-BFFs**
Multiple small BFFs per feature/team.

---

## 📁 Project Structure

```
demo-bff-pattern/
├── shared-service/                 # Backend microservice
│   ├── src/main/java/.../
│   │   ├── SharedServiceApplication.java
│   │   ├── controller/ProductController.java
│   │   ├── model/Product.java
│   │   ├── repository/ProductRepository.java
│   │   └── config/DataInitializer.java  # Sample data
│   └── pom.xml
│
├── web-bff/                        # Web BFF
│   ├── src/main/java/.../
│   │   ├── WebBffApplication.java
│   │   ├── controller/WebProductController.java
│   │   ├── service/WebProductService.java  # Transformation logic
│   │   ├── client/SharedServiceClient.java  # HTTP client
│   │   └── dto/WebProductResponse.java
│   └── pom.xml
│
├── mobile-bff/                     # Mobile BFF
│   ├── src/main/java/.../
│   │   ├── MobileBffApplication.java
│   │   ├── controller/MobileProductController.java
│   │   ├── service/MobileProductService.java  # Transformation logic
│   │   ├── client/SharedServiceClient.java
│   │   └── dto/
│   │       ├── MobileProductResponse.java  # List view
│   │       └── MobileProductDetailResponse.java  # Detail view
│   └── pom.xml
│
└── README.md  # This file
```

---

## 🎓 Key Takeaways

1. **One BFF per client type** - Web, Mobile, Desktop, TV, etc.
2. **BFFs transform data** - Format, aggregate, optimize for client
3. **Shared service provides raw data** - Not exposed to clients directly
4. **Loose coupling** - Change BFF without affecting others
5. **Platform-specific logic** lives in BFFs, not shared service

---

## 🔗 Testing Summary

```bash
# Start all services
cd shared-service && mvn spring-boot:run &
cd web-bff && mvn spring-boot:run &
cd mobile-bff && mvn spring-boot:run &

# Compare responses
curl http://localhost:8100/api/products/1  # Raw
curl http://localhost:8101/api/web/products/1  # Web optimized
curl http://localhost:8102/api/mobile/products/1  # Mobile optimized
```

---

## 📚 Further Reading

- [BFF Pattern - Sam Newman](https://samnewman.io/patterns/architectural/bff/)
- [Netflix BFF Pattern](https://netflixtechblog.com/)
- [Micro Frontends](https://martinfowler.com/articles/micro-frontends.html)

---

**Happy Building!** 🏗️✨
