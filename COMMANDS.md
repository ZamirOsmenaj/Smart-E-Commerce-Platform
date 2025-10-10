# API Commands Reference

Ready-to-use cURL commands for testing the Smart E-Commerce Platform API. Copy and paste these commands to interact with all endpoints.

## Table of Contents

- [Environment Setup](#environment-setup)
- [Authentication Commands](#authentication-commands)
- [Product Commands](#product-commands-proxy-pattern---caching)
- [Order Commands](#order-commands-multiple-patterns)
- [Payment Commands](#payment-commands-strategy--adapter--template-method-patterns)
- [SOAP Commands](#soap-commands)
- [Complete Test Scenarios](#complete-test-scenarios)
- [Monitoring and Debugging Commands](#monitoring-and-debugging-commands)
- [Quick Reference](#quick-reference)

## Environment Setup

Set these variables for easy command customization:

```bash
# Base configuration
export BASE_URL="http://localhost:8080"
export EMAIL="test@example.com"
export PASSWORD="password123"

# These will be set after authentication
export TOKEN=""
export USER_ID=""
export ORDER_ID=""
export PRODUCT_ID=""
```

## Authentication Commands

### Register New User
```bash
curl -X POST $BASE_URL/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"$EMAIL\",
    \"password\": \"$PASSWORD\"
  }"
```

### Login User
```bash
# Login and extract token
RESPONSE=$(curl -s -X POST $BASE_URL/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"$EMAIL\",
    \"password\": \"$PASSWORD\"
  }")

# Extract token (requires jq)
export TOKEN=$(echo $RESPONSE | jq -r '.data.token')
export USER_ID=$(echo $RESPONSE | jq -r '.data.userId')

echo "Token: ${TOKEN:0:20}..."
echo "User ID: $USER_ID"
```

### Alternative Login (Manual Token Extraction)
```bash
curl -X POST $BASE_URL/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"$EMAIL\",
    \"password\": \"$PASSWORD\"
  }"

# Manually copy token from response and set:
# export TOKEN="your_jwt_token_here"
```

## Product Commands (Proxy Pattern - Caching)

### Get All Products
```bash
# First request (cache miss)
curl -X GET $BASE_URL/api/products \
  -H "Content-Type: application/json"
```

### Get All Products (Cache Hit)
```bash
# Second request (cache hit - faster response)
curl -X GET $BASE_URL/api/products \
  -H "Content-Type: application/json"
```

### Get Product by ID
```bash
# First, get a product ID from the list above, then:
export PRODUCT_ID="your_product_id_here"

curl -X GET $BASE_URL/api/products/$PRODUCT_ID \
  -H "Content-Type: application/json"
```

### Create Product (Factory Pattern)
```bash
curl -X POST $BASE_URL/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Gaming Laptop",
    "description": "High-performance gaming laptop with RTX 4080",
    "price": 1899.99,
    "stockQuantity": 15
  }'
```

### Create Multiple Products for Testing
```bash
# Create Smartphone
curl -X POST $BASE_URL/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Smartphone Pro",
    "description": "Latest flagship smartphone with advanced camera",
    "price": 999.99,
    "stockQuantity": 50
  }'

# Create Tablet
curl -X POST $BASE_URL/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Tablet Ultra",
    "description": "Professional tablet for creative work",
    "price": 799.99,
    "stockQuantity": 30
  }'

# Create Headphones
curl -X POST $BASE_URL/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Wireless Headphones",
    "description": "Premium noise-cancelling wireless headphones",
    "price": 299.99,
    "stockQuantity": 100
  }'
```

### Update Product
```bash
curl -X PUT $BASE_URL/api/products/$PRODUCT_ID \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Updated Gaming Laptop",
    "description": "Updated description with new features",
    "price": 1799.99,
    "stockQuantity": 20
  }'
```

### Delete Product
```bash
curl -X DELETE $BASE_URL/api/products/$PRODUCT_ID \
  -H "Authorization: Bearer $TOKEN"
```

## Order Commands (Multiple Patterns)

### Get User Orders
```bash
curl -X GET $BASE_URL/api/orders \
  -H "Authorization: Bearer $TOKEN"
```

### Create Order (Chain of Responsibility + Factory + Command Patterns)
```bash
# Get product IDs first, then create order
PRODUCT_RESPONSE=$(curl -s -X GET $BASE_URL/api/products)
PRODUCT_ID_1=$(echo $PRODUCT_RESPONSE | jq -r '.data[0].id')
PRODUCT_ID_2=$(echo $PRODUCT_RESPONSE | jq -r '.data[1].id')

# Create order with multiple items
ORDER_RESPONSE=$(curl -s -X POST $BASE_URL/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"items\": [
      {
        \"productId\": \"$PRODUCT_ID_1\",
        \"quantity\": 2
      },
      {
        \"productId\": \"$PRODUCT_ID_2\",
        \"quantity\": 1
      }
    ]
  }")

# Extract order ID
export ORDER_ID=$(echo $ORDER_RESPONSE | jq -r '.data.id')
echo "Created Order ID: $ORDER_ID"
```

### Create Simple Order (Single Item)
```bash
curl -X POST $BASE_URL/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"items\": [
      {
        \"productId\": \"$PRODUCT_ID_1\",
        \"quantity\": 1
      }
    ]
  }"
```

### Get Available Actions (State Pattern)
```bash
curl -X GET $BASE_URL/api/orders/$ORDER_ID/available-actions \
  -H "Authorization: Bearer $TOKEN"
```

### Check State Transition (State Pattern)
```bash
# Check if order can transition to PAID
curl -X GET $BASE_URL/api/orders/$ORDER_ID/can-transition-to/PAID \
  -H "Authorization: Bearer $TOKEN"

# Check if order can transition to CANCELLED
curl -X GET $BASE_URL/api/orders/$ORDER_ID/can-transition-to/CANCELLED \
  -H "Authorization: Bearer $TOKEN"
```

### Cancel Order (Command Pattern)
```bash
curl -X POST $BASE_URL/api/orders/$ORDER_ID/cancel \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "reason": "Customer requested cancellation"
  }'
```

### Undo Last Command (Command Pattern)
```bash
curl -X POST $BASE_URL/api/orders/undo-last \
  -H "Authorization: Bearer $TOKEN"
```

### Get Undo Information (Command Pattern)
```bash
curl -X GET $BASE_URL/api/orders/undo-info \
  -H "Authorization: Bearer $TOKEN"
```

## Payment Commands (Strategy + Adapter + Template Method Patterns)

### Process Payment with Default Provider (Mock)
```bash
curl -X POST $BASE_URL/api/payments/$ORDER_ID \
  -H "Authorization: Bearer $TOKEN"
```

### Process Payment with Stripe Adapter
```bash
curl -X POST "$BASE_URL/api/payments/$ORDER_ID?provider=stripePayment" \
  -H "Authorization: Bearer $TOKEN"
```

### Process Payment with PayPal Adapter
```bash
curl -X POST "$BASE_URL/api/payments/$ORDER_ID?provider=paypalPayment" \
  -H "Authorization: Bearer $TOKEN"
```

### Process Payment with Credit Card Strategy
```bash
curl -X POST "$BASE_URL/api/payments/$ORDER_ID?provider=creditCardPayment" \
  -H "Authorization: Bearer $TOKEN"
```

## SOAP Commands

### Get Order via SOAP
```bash
curl -X POST $BASE_URL/ws \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H "SOAPAction: getOrder" \
  -d "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" 
                  xmlns:ord=\"http://example.com/ecommerce/orders\">
   <soapenv:Header>
      <ord:authToken>Bearer $TOKEN</ord:authToken>
   </soapenv:Header>
   <soapenv:Body>
      <ord:getOrderRequest>
         <ord:orderId>$ORDER_ID</ord:orderId>
      </ord:getOrderRequest>
   </soapenv:Body>
</soapenv:Envelope>"
```

### Create Order via SOAP
```bash
curl -X POST $BASE_URL/ws \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H "SOAPAction: createOrder" \
  -d "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" 
                  xmlns:ord=\"http://example.com/ecommerce/orders\">
   <soapenv:Header>
      <ord:authToken>Bearer $TOKEN</ord:authToken>
   </soapenv:Header>
   <soapenv:Body>
      <ord:createOrderRequest>
         <ord:items>
            <ord:item>
               <ord:productId>$PRODUCT_ID_1</ord:productId>
               <ord:quantity>1</ord:quantity>
            </ord:item>
         </ord:items>
      </ord:createOrderRequest>
   </soapenv:Body>
</soapenv:Envelope>"
```

### Update Order Status via SOAP
```bash
curl -X POST $BASE_URL/ws \
  -H "Content-Type: text/xml; charset=utf-8" \
  -H "SOAPAction: updateOrderStatus" \
  -d "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" 
                  xmlns:ord=\"http://example.com/ecommerce/orders\">
   <soapenv:Header>
      <ord:authToken>Bearer $TOKEN</ord:authToken>
   </soapenv:Header>
   <soapenv:Body>
      <ord:updateOrderStatusRequest>
         <ord:orderId>$ORDER_ID</ord:orderId>
         <ord:status>PAID</ord:status>
      </ord:updateOrderStatusRequest>
   </soapenv:Body>
</soapenv:Envelope>"
```

### Get WSDL
```bash
curl -X GET $BASE_URL/ws/orders.wsdl
```

## Complete Test Scenarios

### Scenario 1: Full User Journey
```bash
#!/bin/bash
echo "=== Complete User Journey Test ==="

# 1. Register and login
echo "1. Registering user..."
REGISTER_RESPONSE=$(curl -s -X POST $BASE_URL/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"journey@test.com\",\"password\":\"test123\"}")

TOKEN=$(echo $REGISTER_RESPONSE | jq -r '.data.token')
echo "Token obtained: ${TOKEN:0:20}..."

# 2. Create a product
echo "2. Creating product..."
PRODUCT_RESPONSE=$(curl -s -X POST $BASE_URL/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Test Product",
    "description": "Product for testing",
    "price": 99.99,
    "stockQuantity": 10
  }')

PRODUCT_ID=$(echo $PRODUCT_RESPONSE | jq -r '.data.id')
echo "Product created: $PRODUCT_ID"

# 3. Create order
echo "3. Creating order..."
ORDER_RESPONSE=$(curl -s -X POST $BASE_URL/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"items\": [{
      \"productId\": \"$PRODUCT_ID\",
      \"quantity\": 2
    }]
  }")

ORDER_ID=$(echo $ORDER_RESPONSE | jq -r '.data.id')
echo "Order created: $ORDER_ID"

# 4. Check available actions
echo "4. Checking available actions..."
curl -s -X GET $BASE_URL/api/orders/$ORDER_ID/available-actions \
  -H "Authorization: Bearer $TOKEN" | jq '.data.actions'

# 5. Process payment
echo "5. Processing payment..."
curl -s -X POST $BASE_URL/api/payments/$ORDER_ID \
  -H "Authorization: Bearer $TOKEN" | jq '.message'

echo "=== Journey Complete ==="
```

### Scenario 2: Design Pattern Demonstration
```bash
#!/bin/bash
echo "=== Design Pattern Demonstration ==="

# Proxy Pattern - Cache Performance Test
echo "1. Testing Proxy Pattern (Caching)..."
echo "First request (cache miss):"
time curl -s $BASE_URL/api/products > /dev/null
echo "Second request (cache hit):"
time curl -s $BASE_URL/api/products > /dev/null

# Strategy Pattern - Different Payment Providers
echo "2. Testing Strategy Pattern (Payment Providers)..."
echo "Mock Payment:"
curl -s -X POST $BASE_URL/api/payments/$ORDER_ID \
  -H "Authorization: Bearer $TOKEN" | jq '.message'

echo "Stripe Payment:"
curl -s -X POST "$BASE_URL/api/payments/$ORDER_ID?provider=stripePayment" \
  -H "Authorization: Bearer $TOKEN" | jq '.message'

# Command Pattern - Undo Operations
echo "3. Testing Command Pattern (Undo)..."
echo "Cancelling order:"
curl -s -X POST $BASE_URL/api/orders/$ORDER_ID/cancel \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"reason":"Testing undo"}' | jq '.message'

echo "Undoing cancellation:"
curl -s -X POST $BASE_URL/api/orders/undo-last \
  -H "Authorization: Bearer $TOKEN" | jq '.message'

echo "=== Pattern Demo Complete ==="
```

### Scenario 3: Error Handling Test
```bash
#!/bin/bash
echo "=== Error Handling Test ==="

# Test authentication error
echo "1. Testing authentication error..."
curl -X GET $BASE_URL/api/orders

# Test invalid product ID
echo "2. Testing invalid product ID..."
curl -X GET $BASE_URL/api/products/invalid-uuid

# Test order creation with invalid product
echo "3. Testing order with invalid product..."
curl -X POST $BASE_URL/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "items": [{
      "productId": "00000000-0000-0000-0000-000000000000",
      "quantity": 1
    }]
  }'

echo "=== Error Test Complete ==="
```

## Monitoring and Debugging Commands

### Health Check
```bash
curl -X GET $BASE_URL/actuator/health
```

### Application Info
```bash
curl -X GET $BASE_URL/actuator/info
```

### Check Redis Cache
```bash
# Connect to Redis container
docker exec -it ecommerce-redis redis-cli

# Redis commands:
# KEYS product:*          # List cached products
# GET product:PRODUCT_ID  # Get specific product cache
# KEYS products:*         # List product list caches
# FLUSHALL               # Clear all cache
```

### Check Database
```bash
# Connect to PostgreSQL container
docker exec -it ecommerce-db psql -U postgres -d ecommerce

# SQL commands:
# \dt                    # List tables
# SELECT * FROM users;   # View users
# SELECT * FROM orders;  # View orders
# SELECT * FROM products; # View products
```

### View Application Logs
```bash
# View all logs
docker logs ecommerce-app

# Follow logs in real-time
docker logs ecommerce-app -f

# Filter by pattern (Proxy pattern logs)
docker logs ecommerce-app | grep "PROXY:"

# Filter by pattern (Command pattern logs)
docker logs ecommerce-app | grep "COMMAND:"

# Filter by pattern (Observer pattern logs)
docker logs ecommerce-app | grep "OBSERVER:"
```

## Quick Reference

### Common HTTP Status Codes
- `200 OK` - Success
- `400 Bad Request` - Invalid request
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Access denied
- `404 Not Found` - Resource not found
- `409 Conflict` - Business rule violation

### Order Status Values
- `PENDING` - Order created, awaiting payment
- `PAID` - Payment processed successfully
- `CANCELLED` - Order cancelled

### Payment Providers
- `mockPayment` - Default mock provider
- `stripePayment` - Stripe adapter
- `paypalPayment` - PayPal adapter
- `creditCardPayment` - Credit card strategy

---

## 🔗 Navigation

| Previous | Home | Documentation |
|----------|------|---------------|
| [← Development Guide](docs/DEVELOPMENT_GUIDE.md) | [🏠 Home](README.md) | [📚 All Docs](README.md#-documentation) |

**Quick Links:**
- [🔐 Authentication Commands](#authentication-commands)
- [🛍️ Product Commands](#product-commands-proxy-pattern---caching)
- [📦 Order Commands](#order-commands-multiple-patterns)
- [💳 Payment Commands](#payment-commands-strategy--adapter--template-method-patterns)
- [🧼 SOAP Commands](#soap-commands)
- [🎯 Complete Test Scenarios](#complete-test-scenarios)

**Note**: Replace placeholder values (PRODUCT_ID, ORDER_ID, TOKEN) with actual values from API responses. Use `jq` for JSON parsing or manually extract values from responses.

[⬆️ Back to Top](#api-commands-reference)