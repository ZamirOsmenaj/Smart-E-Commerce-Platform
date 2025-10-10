# Testing Guide

This guide provides step-by-step instructions for setting up, running, and testing the Smart E-Commerce Platform.

## Table of Contents

- [Quick Start](#quick-start)
- [Environment Setup](#environment-setup)
- [Testing Scenarios](#testing-scenarios)
- [API Testing Examples](#api-testing-examples)
- [SOAP Testing](#soap-testing)
- [Troubleshooting](#troubleshooting)

## Quick Start

### Prerequisites
- Docker and Docker Compose installed
- curl or Postman for API testing
- Optional: SoapUI for SOAP testing

### 1. Start the Application
```bash
# Clone the repository
git clone <repository-url>
cd smart-ecommerce-platform

# Start all services
docker-compose up -d

# Verify services are running
docker-compose ps
```

### 2. Health Check
```bash
# Check application health
curl http://localhost:8080/actuator/health

# Expected response:
# {"status":"UP"}
```

### 3. Quick Test Flow
```bash
# 1. Register a user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# 2. Extract token from response and test protected endpoint
# (Replace TOKEN with actual token from step 1)
curl -X GET http://localhost:8080/api/orders \
  -H "Authorization: Bearer TOKEN"
```

## Environment Setup

### Docker Services
The application runs three main services:
|
 Service | Port | Purpose |
|---------|------|---------|
| PostgreSQL | 5432 | Primary database |
| Redis | 6379 | Caching layer |
| Application | 8080 | Main API server |

### Environment Variables
```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/ecommerce
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# Redis Configuration
SPRING_REDIS_HOST=redis
SPRING_REDIS_PORT=6379

# JWT Configuration
JWT_SECRET=mysuperlongsecretkeyforjwt1234567890
JWT_EXPIRATION=3600000
```

### Service Verification
```bash
# Check PostgreSQL
docker exec -it ecommerce-db psql -U postgres -d ecommerce -c "\dt"

# Check Redis
docker exec -it ecommerce-redis redis-cli ping

# Check Application Logs
docker logs ecommerce-app
```

## Testing Scenarios

### Scenario 1: User Registration and Authentication
This scenario tests the authentication flow and JWT token generation.

```bash
# Step 1: Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "securePassword123"
  }'

# Expected Response:
# {
#   "success": true,
#   "message": "User registered successfully",
#   "data": {
#     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
#     "userId": "123e4567-e89b-12d3-a456-426614174000",
#     "email": "john.doe@example.com"
#   }
# }

# Step 2: Login with the same credentials
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "securePassword123"
  }'
```

### Scenario 2: Product Management (Proxy Pattern Testing)
This scenario demonstrates the caching proxy pattern in action.

```bash
# Step 1: Get all products (cache miss - loads from database)
curl -X GET http://localhost:8080/api/products

# Step 2: Get all products again (cache hit - loads from Redis)
curl -X GET http://localhost:8080/api/products

# Step 3: Create a new product (requires authentication)
# Replace TOKEN with your JWT token
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{
    "name": "Gaming Laptop",
    "description": "High-performance gaming laptop with RTX graphics",
    "price": 1299.99,
    "stockQuantity": 25
  }'

# Step 4: Get products again (cache invalidated, fresh data)
curl -X GET http://localhost:8080/api/products
```

### Scenario 3: Order Creation and Management (Multiple Patterns)
This scenario tests Factory, Chain of Responsibility, State, and Command patterns.

```bash
# Step 1: Create an order (Chain of Responsibility validation)
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{
    "items": [
      {
        "productId": "PRODUCT_ID_FROM_STEP_2",
        "quantity": 2
      }
    ]
  }'

# Step 2: Check available actions (State Pattern)
curl -X GET http://localhost:8080/api/orders/ORDER_ID/available-actions \
  -H "Authorization: Bearer TOKEN"

# Step 3: Check state transition possibility
curl -X GET http://localhost:8080/api/orders/ORDER_ID/can-transition-to/PAID \
  -H "Authorization: Bearer TOKEN"

# Step 4: Cancel the order (Command Pattern)
curl -X POST http://localhost:8080/api/orders/ORDER_ID/cancel \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{
    "reason": "Customer changed mind"
  }'

# Step 5: Undo the cancellation (Command Pattern)
curl -X POST http://localhost:8080/api/orders/undo-last \
  -H "Authorization: Bearer TOKEN"
```

### Scenario 4: Payment Processing (Strategy and Adapter Patterns)
This scenario tests different payment providers and the adapter pattern.

```bash
# Step 1: Process payment with default provider (Mock)
curl -X POST http://localhost:8080/api/payments/ORDER_ID \
  -H "Authorization: Bearer TOKEN"

# Step 2: Process payment with Stripe adapter
curl -X POST "http://localhost:8080/api/payments/ORDER_ID?provider=stripePayment" \
  -H "Authorization: Bearer TOKEN"

# Step 3: Process payment with PayPal adapter
curl -X POST "http://localhost:8080/api/payments/ORDER_ID?provider=paypalPayment" \
  -H "Authorization: Bearer TOKEN"
```

## API Testing Examples

### Complete User Journey Test Script
Save this as `test_user_journey.sh`:

```bash
#!/bin/bash

BASE_URL="http://localhost:8080"
EMAIL="testuser@example.com"
PASSWORD="testpass123"

echo "=== Smart E-Commerce Platform Test ==="

# 1. Register User
echo "1. Registering user..."
REGISTER_RESPONSE=$(curl -s -X POST $BASE_URL/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}")

TOKEN=$(echo $REGISTER_RESPONSE | jq -r '.data.token')
echo "Token: ${TOKEN:0:20}..."

# 2. Get Products
echo "2. Getting products..."
curl -s -X GET $BASE_URL/api/products | jq '.data[0]'

# 3. Create Order
echo "3. Creating order..."
PRODUCT_ID=$(curl -s -X GET $BASE_URL/api/products | jq -r '.data[0].id')
ORDER_RESPONSE=$(curl -s -X POST $BASE_URL/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{\"items\":[{\"productId\":\"$PRODUCT_ID\",\"quantity\":1}]}")

ORDER_ID=$(echo $ORDER_RESPONSE | jq -r '.data.id')
echo "Order ID: $ORDER_ID"

# 4. Process Payment
echo "4. Processing payment..."
curl -s -X POST $BASE_URL/api/payments/$ORDER_ID \
  -H "Authorization: Bearer $TOKEN" | jq '.message'

echo "=== Test Complete ==="
```

### Performance Testing
Test the caching proxy pattern performance:

```bash
#!/bin/bash

echo "=== Cache Performance Test ==="

# First request (cache miss)
echo "First request (cache miss):"
time curl -s http://localhost:8080/api/products > /dev/null

# Second request (cache hit)
echo "Second request (cache hit):"
time curl -s http://localhost:8080/api/products > /dev/null

# Third request (cache hit)
echo "Third request (cache hit):"
time curl -s http://localhost:8080/api/products > /dev/null
```

## SOAP Testing

### Using curl for SOAP Requests

#### Get Order via SOAP
```bash
curl -X POST http://localhost:8080/ws \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H "SOAPAction: getOrder" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:ord="http://example.com/ecommerce/orders">
   <soapenv:Header>
      <ord:authToken>Bearer YOUR_JWT_TOKEN</ord:authToken>
   </soapenv:Header>
   <soapenv:Body>
      <ord:getOrderRequest>
         <ord:orderId>YOUR_ORDER_ID</ord:orderId>
      </ord:getOrderRequest>
   </soapenv:Body>
</soapenv:Envelope>'
```

#### Create Order via SOAP
```bash
curl -X POST http://localhost:8080/ws \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H "SOAPAction: createOrder" \
  -d '<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:ord="http://example.com/ecommerce/orders">
   <soapenv:Header>
      <ord:authToken>Bearer YOUR_JWT_TOKEN</ord:authToken>
   </soapenv:Header>
   <soapenv:Body>
      <ord:createOrderRequest>
         <ord:items>
            <ord:item>
               <ord:productId>YOUR_PRODUCT_ID</ord:productId>
               <ord:quantity>1</ord:quantity>
            </ord:item>
         </ord:items>
      </ord:createOrderRequest>
   </soapenv:Body>
</soapenv:Envelope>'
```

### WSDL Access
```bash
# Get WSDL definition
curl http://localhost:8080/ws/orders.wsdl
```

## Troubleshooting

### Common Issues and Solutions

#### 1. Services Not Starting
```bash
# Check service status
docker-compose ps

# View logs
docker-compose logs app
docker-compose logs postgres
docker-compose logs redis

# Restart services
docker-compose restart
```

#### 2. Database Connection Issues
```bash
# Check PostgreSQL connectivity
docker exec -it ecommerce-db pg_isready -U postgres

# Check database exists
docker exec -it ecommerce-db psql -U postgres -l

# Reset database
docker-compose down -v
docker-compose up -d
```

#### 3. Redis Connection Issues
```bash
# Test Redis connectivity
docker exec -it ecommerce-redis redis-cli ping

# Check Redis logs
docker logs ecommerce-redis

# Clear Redis cache
docker exec -it ecommerce-redis redis-cli FLUSHALL
```

#### 4. Authentication Issues
```bash
# Check JWT token format
echo "YOUR_TOKEN" | base64 -d

# Verify token hasn't expired (default: 1 hour)
# Re-login to get fresh token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"your@email.com","password":"yourpassword"}'
```

#### 5. API Response Issues
```bash
# Check application health
curl http://localhost:8080/actuator/health

# View detailed application logs
docker logs ecommerce-app -f

# Check specific pattern logging
docker logs ecommerce-app | grep "PROXY:"
docker logs ecommerce-app | grep "ADAPTER:"
docker logs ecommerce-app | grep "COMMAND:"
```

### Design Pattern Verification

#### Verify Proxy Pattern (Caching)
```bash
# Enable debug logging for proxy pattern
# Check logs for cache hits/misses
docker logs ecommerce-app | grep "PROXY:"

# Expected output:
# PROXY: Cache miss for products list - fetching from delegate
# PROXY: Cached products list with 5 items
# PROXY: Cache hit for products list
```

#### Verify Observer Pattern (Notifications)
```bash
# Create and cancel an order, check for observer notifications
docker logs ecommerce-app | grep "OBSERVER:"

# Expected output:
# Publishing order status change: Order abc123 from PENDING to CANCELLED
# OBSERVER: OrderNotificationObserver - Processing status change
# OBSERVER: InventoryReleaseObserver - Releasing inventory
```

#### Verify Command Pattern (Undo Operations)
```bash
# Test command undo functionality
curl -X POST http://localhost:8080/api/orders/ORDER_ID/cancel \
  -H "Authorization: Bearer TOKEN" \
  -d '{"reason":"test"}'

curl -X POST http://localhost:8080/api/orders/undo-last \
  -H "Authorization: Bearer TOKEN"

# Check logs for command execution
docker logs ecommerce-app | grep "COMMAND:"
```

### Performance Monitoring

#### Monitor Cache Performance
```bash
# Redis statistics
docker exec -it ecommerce-redis redis-cli INFO stats

# Key metrics to watch:
# - keyspace_hits: Cache hits
# - keyspace_misses: Cache misses
# - used_memory: Memory usage
```

#### Monitor Database Performance
```bash
# PostgreSQL statistics
docker exec -it ecommerce-db psql -U postgres -d ecommerce \
  -c "SELECT schemaname,tablename,n_tup_ins,n_tup_upd,n_tup_del FROM pg_stat_user_tables;"
```

### Load Testing
```bash
# Simple load test with curl
for i in {1..10}; do
  curl -s http://localhost:8080/api/products > /dev/null &
done
wait

echo "Load test complete - check cache performance"
```

---

## 🔗 Navigation

| Previous | Home | Next |
|----------|------|------|
| [← API Reference](API_REFERENCE.md) | [🏠 Home](../README.md) | [Development Guide →](DEVELOPMENT_GUIDE.md) |

**Quick Links:**
- [🚀 Quick Start](#quick-start)
- [🔧 Environment Setup](#environment-setup)
- [🎯 Testing Scenarios](#testing-scenarios)
- [🧪 API Testing Examples](#api-testing-examples)
- [🧼 SOAP Testing](#soap-testing)
- [🔍 Troubleshooting](#troubleshooting)

**Need Commands?** Check out the [Commands Reference](../COMMANDS.md) for ready-to-use cURL commands!

[⬆️ Back to Top](#testing-guide)