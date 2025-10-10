# API Reference Guide

This document provides comprehensive documentation for all REST and SOAP endpoints in the Smart E-Commerce Platform.

## Table of Contents

- [Authentication](#authentication)
- [REST API Endpoints](#rest-api-endpoints)
- [SOAP Web Services](#soap-web-services)
- [Error Handling](#error-handling)
- [Response Format](#response-format)
- [Status Codes](#status-codes)

## Authentication

The API uses JWT (JSON Web Token) based authentication. All protected endpoints require a valid JWT token in the Authorization header.

### Authentication Header Format
```
Authorization: Bearer <jwt-token>
```

### Token Lifecycle
- **Expiration**: Configurable (default: 1 hour)
- **Refresh**: Not implemented (re-login required)
- **Validation**: On every protected request

## REST API Endpoints

### Authentication Endpoints

#### Register User
**Endpoint**: `POST /api/auth/register`  
**Description**: Register a new user and receive authentication token  
**Authentication**: Not required

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

**Response**:
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "email": "user@example.com"
  },
  "errorCode": null
}
```

#### Login User
**Endpoint**: `POST /api/auth/login`  
**Description**: Authenticate existing user and receive token  
**Authentication**: Not required

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

**Response**:
```json
{
  "success": true,
  "message": "User logged in successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "email": "user@example.com"
  },
  "errorCode": null
}
```

### Product Endpoints

#### Get All Products
**Endpoint**: `GET /api/products`  
**Description**: Retrieve all products (uses Proxy Pattern for caching)  
**Authentication**: Not required

**Response**:
```json
{
  "success": true,
  "message": "Products retrieved successfully",
  "data": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "name": "Laptop",
      "description": "High-performance laptop",
      "price": 999.99,
      "stockQuantity": 50,
      "createdAt": "2024-01-15T10:30:00Z"
    }
  ],
  "errorCode": null
}
```

#### Get Product by ID
**Endpoint**: `GET /api/products/{id}`  
**Description**: Retrieve specific product by UUID  
**Authentication**: Not required

**Path Parameters**:
- `id` (UUID): Product identifier

**Response**:
```json
{
  "success": true,
  "message": "Product retrieved successfully",
  "data": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 999.99,
    "stockQuantity": 50,
    "createdAt": "2024-01-15T10:30:00Z"
  },
  "errorCode": null
}
```

#### Create Product
**Endpoint**: `POST /api/products`  
**Description**: Create new product (uses Factory Pattern)  
**Authentication**: Required

**Request Body**:
```json
{
  "name": "Smartphone",
  "description": "Latest model smartphone",
  "price": 699.99,
  "stockQuantity": 100
}
```

**Response**:
```json
{
  "success": true,
  "message": "Product created successfully",
  "data": {
    "id": "456e7890-e89b-12d3-a456-426614174001",
    "name": "Smartphone",
    "description": "Latest model smartphone",
    "price": 699.99,
    "stockQuantity": 100,
    "createdAt": "2024-01-15T11:00:00Z"
  },
  "errorCode": null
}
```

#### Update Product
**Endpoint**: `PUT /api/products/{id}`  
**Description**: Update existing product  
**Authentication**: Required

**Path Parameters**:
- `id` (UUID): Product identifier

**Request Body**:
```json
{
  "name": "Updated Smartphone",
  "description": "Updated description",
  "price": 649.99,
  "stockQuantity": 75
}
```

#### Delete Product
**Endpoint**: `DELETE /api/products/{id}`  
**Description**: Delete product by ID  
**Authentication**: Required

**Path Parameters**:
- `id` (UUID): Product identifier

**Response**:
```json
{
  "success": true,
  "message": "Product deleted successfully",
  "data": null,
  "errorCode": null
}
```

### Order Endpoints

#### Get User Orders
**Endpoint**: `GET /api/orders`  
**Description**: Retrieve all orders for authenticated user  
**Authentication**: Required

**Response**:
```json
{
  "success": true,
  "message": "Orders retrieved successfully",
  "data": [
    {
      "id": "789e0123-e89b-12d3-a456-426614174002",
      "userId": "123e4567-e89b-12d3-a456-426614174000",
      "total": 1299.98,
      "status": "PENDING",
      "createdAt": "2024-01-15T12:00:00Z",
      "items": [
        {
          "productId": "123e4567-e89b-12d3-a456-426614174000",
          "quantity": 1,
          "price": 999.99
        },
        {
          "productId": "456e7890-e89b-12d3-a456-426614174001",
          "quantity": 1,
          "price": 699.99
        }
      ]
    }
  ],
  "errorCode": null
}
```

#### Create Order
**Endpoint**: `POST /api/orders`  
**Description**: Create new order (uses Command Pattern, Chain of Responsibility for validation)  
**Authentication**: Required

**Request Body**:
```json
{
  "items": [
    {
      "productId": "123e4567-e89b-12d3-a456-426614174000",
      "quantity": 2
    },
    {
      "productId": "456e7890-e89b-12d3-a456-426614174001",
      "quantity": 1
    }
  ]
}
```

**Response**:
```json
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "id": "abc12345-e89b-12d3-a456-426614174003",
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "total": 2699.97,
    "status": "PENDING",
    "createdAt": "2024-01-15T13:00:00Z",
    "items": [
      {
        "productId": "123e4567-e89b-12d3-a456-426614174000",
        "quantity": 2,
        "price": 999.99
      },
      {
        "productId": "456e7890-e89b-12d3-a456-426614174001",
        "quantity": 1,
        "price": 699.99
      }
    ]
  },
  "errorCode": null
}
```

#### Cancel Order
**Endpoint**: `POST /api/orders/{orderId}/cancel`  
**Description**: Cancel order (uses Command Pattern, State Pattern for validation)  
**Authentication**: Required

**Path Parameters**:
- `orderId` (UUID): Order identifier

**Request Body**:
```json
{
  "reason": "Customer requested cancellation"
}
```

**Response**:
```json
{
  "success": true,
  "message": "Order cancelled successfully",
  "data": {
    "id": "abc12345-e89b-12d3-a456-426614174003",
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "total": 2699.97,
    "status": "CANCELLED",
    "createdAt": "2024-01-15T13:00:00Z",
    "items": [...]
  },
  "errorCode": null
}
```

#### Get Available Actions
**Endpoint**: `GET /api/orders/{orderId}/available-actions`  
**Description**: Get available actions for order (uses State Pattern)  
**Authentication**: Required

**Path Parameters**:
- `orderId` (UUID): Order identifier

**Response**:
```json
{
  "success": true,
  "message": "Available actions retrieved successfully",
  "data": {
    "orderId": "abc12345-e89b-12d3-a456-426614174003",
    "actions": "Available actions: Process Payment, Cancel Order"
  },
  "errorCode": null
}
```

#### Check State Transition
**Endpoint**: `GET /api/orders/{orderId}/can-transition-to/{targetStatus}`  
**Description**: Check if order can transition to target status (uses State Pattern)  
**Authentication**: Required

**Path Parameters**:
- `orderId` (UUID): Order identifier
- `targetStatus` (OrderStatus): Target status (PENDING, PAID, CANCELLED)

**Response**:
```json
{
  "success": true,
  "message": "Transition check completed successfully",
  "data": {
    "orderId": "abc12345-e89b-12d3-a456-426614174003",
    "targetStatus": "PAID",
    "canTransition": true
  },
  "errorCode": null
}
```

#### Undo Last Command
**Endpoint**: `POST /api/orders/undo-last`  
**Description**: Undo the last undoable command (uses Command Pattern)  
**Authentication**: Required

**Response**:
```json
{
  "success": true,
  "message": "Command undone successfully",
  "data": {
    "success": true,
    "message": "Order cancellation undone successfully",
    "data": {
      "orderId": "abc12345-e89b-12d3-a456-426614174003",
      "previousStatus": "CANCELLED",
      "newStatus": "PENDING"
    }
  },
  "errorCode": null
}
```

#### Get Undo Information
**Endpoint**: `GET /api/orders/undo-info`  
**Description**: Get information about undoable commands  
**Authentication**: Required

**Response**:
```json
{
  "success": true,
  "message": "Undo information retrieved successfully",
  "data": {
    "undoableCommandCount": 1,
    "lastUndoableCommand": "Available",
    "hasUndoableCommands": true,
    "historySummary": "Last command: CancelOrderCommand - can be undone"
  },
  "errorCode": null
}
```

### Payment Endpoints

#### Process Payment
**Endpoint**: `POST /api/payments/{orderId}`  
**Description**: Process payment for order (uses Strategy Pattern, Adapter Pattern, Template Method)  
**Authentication**: Required

**Path Parameters**:
- `orderId` (UUID): Order identifier

**Query Parameters**:
- `provider` (string, optional): Payment provider (`mockPayment`, `stripePayment`, `paypalPayment`)
  - Default: `mockPayment`

**Example Request**:
```
POST /api/payments/abc12345-e89b-12d3-a456-426614174003?provider=stripePayment
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response**:
```json
{
  "success": true,
  "message": "Payment processed successfully",
  "data": {
    "orderId": "abc12345-e89b-12d3-a456-426614174003",
    "orderStatus": "PAID"
  },
  "errorCode": null
}
```

## SOAP Web Services

The application provides SOAP endpoints for legacy system integration.

**WSDL Location**: `http://localhost:8080/ws/orders.wsdl`  
**Namespace**: `http://example.com/ecommerce/orders`

### SOAP Authentication
SOAP endpoints require JWT token in the SOAP header:

```xml
<soapenv:Header>
    <ord:authToken xmlns:ord="http://example.com/ecommerce/orders">
        Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
    </ord:authToken>
</soapenv:Header>
```

### Get Order (SOAP)
**Operation**: `getOrder`  
**Description**: Retrieve order details via SOAP

**Request**:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:ord="http://example.com/ecommerce/orders">
   <soapenv:Header>
      <ord:authToken>Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...</ord:authToken>
   </soapenv:Header>
   <soapenv:Body>
      <ord:getOrderRequest>
         <ord:orderId>abc12345-e89b-12d3-a456-426614174003</ord:orderId>
      </ord:getOrderRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

**Response**:
```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
   <soap:Body>
      <ns2:getOrderResponse xmlns:ns2="http://example.com/ecommerce/orders">
         <ns2:order>
            <ns2:id>abc12345-e89b-12d3-a456-426614174003</ns2:id>
            <ns2:userId>123e4567-e89b-12d3-a456-426614174000</ns2:userId>
            <ns2:totalAmount>2699.97</ns2:totalAmount>
            <ns2:status>PENDING</ns2:status>
            <ns2:createdAt>2024-01-15T13:00:00Z</ns2:createdAt>
            <ns2:items>
               <ns2:item>
                  <ns2:productId>123e4567-e89b-12d3-a456-426614174000</ns2:productId>
                  <ns2:quantity>2</ns2:quantity>
               </ns2:item>
            </ns2:items>
         </ns2:order>
      </ns2:getOrderResponse>
   </soap:Body>
</soap:Envelope>
```

### Create Order (SOAP)
**Operation**: `createOrder`  
**Description**: Create new order via SOAP (uses same Command Pattern as REST)

**Request**:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:ord="http://example.com/ecommerce/orders">
   <soapenv:Header>
      <ord:authToken>Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...</ord:authToken>
   </soapenv:Header>
   <soapenv:Body>
      <ord:createOrderRequest>
         <ord:items>
            <ord:item>
               <ord:productId>123e4567-e89b-12d3-a456-426614174000</ord:productId>
               <ord:quantity>1</ord:quantity>
            </ord:item>
         </ord:items>
      </ord:createOrderRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

### Update Order Status (SOAP)
**Operation**: `updateOrderStatus`  
**Description**: Update order status via SOAP

**Request**:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
                  xmlns:ord="http://example.com/ecommerce/orders">
   <soapenv:Header>
      <ord:authToken>Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...</ord:authToken>
   </soapenv:Header>
   <soapenv:Body>
      <ord:updateOrderStatusRequest>
         <ord:orderId>abc12345-e89b-12d3-a456-426614174003</ord:orderId>
         <ord:status>PAID</ord:status>
      </ord:updateOrderStatusRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

## Error Handling

### Standard Error Response Format
```json
{
  "success": false,
  "message": "Detailed error message",
  "data": null,
  "errorCode": "ERROR_CODE_CONSTANT"
}
```

### Common Error Codes
- `REGISTRATION_FAILED` - User registration failed
- `LOGIN_FAILED` - Authentication failed
- `ORDER_CREATION_FAILED` - Order creation failed
- `ORDER_CANCELLATION_FAILED` - Order cancellation failed
- `PAYMENT_FAILED` - Payment processing failed
- `PRODUCT_CREATION_FAILED` - Product creation failed
- `PRODUCT_NOT_FOUND` - Product not found
- `ORDER_NOT_FOUND` - Order not found
- `UNDO_FAILED` - Command undo failed

### Authentication Errors
- `401 Unauthorized` - Missing or invalid JWT token
- `403 Forbidden` - Valid token but insufficient permissions

### Validation Errors
- `400 Bad Request` - Invalid request data
- `422 Unprocessable Entity` - Validation failed

### Business Logic Errors
- `409 Conflict` - State transition not allowed
- `404 Not Found` - Resource not found

## Response Format

### Success Response Structure
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { /* Response data */ },
  "errorCode": null
}
```

### Error Response Structure
```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "errorCode": "ERROR_CODE"
}
```

## Status Codes

### HTTP Status Codes Used
- `200 OK` - Successful operation
- `201 Created` - Resource created successfully
- `400 Bad Request` - Invalid request
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Access denied
- `404 Not Found` - Resource not found
- `409 Conflict` - Business rule violation
- `422 Unprocessable Entity` - Validation error
- `500 Internal Server Error` - Server error

### Order Status Values
- `PENDING` - Order created, awaiting payment
- `PAID` - Payment processed successfully
- `CANCELLED` - Order cancelled

### Design Pattern Integration

The API endpoints demonstrate various design patterns:

- **Factory Pattern**: Entity creation in POST endpoints
- **Adapter Pattern**: Payment provider integration
- **Proxy Pattern**: Product caching in GET endpoints
- **Facade Pattern**: Response mapping via MapperFacade
- **Chain of Responsibility**: Order validation pipeline
- **Observer Pattern**: Status change notifications
- **Strategy Pattern**: Payment provider selection
- **Template Method Pattern**: Payment processing workflow
- **State Pattern**: Order state validation
- **Command Pattern**: Undoable operations
- **Decorator Pattern**: Notification channel composition

---

## 🔗 Navigation

| Previous | Home | Next |
|----------|------|------|
| [← Technology Stack](TECHNOLOGY_STACK.md) | [🏠 Home](../README.md) | [Testing Guide →](TESTING_GUIDE.md) |

**Quick Links:**
- [🔐 Authentication](#authentication)
- [🛍️ REST API Endpoints](#rest-api-endpoints)
- [🧼 SOAP Web Services](#soap-web-services)
- [❌ Error Handling](#error-handling)
- [📋 Response Format](#response-format)
- [📊 Status Codes](#status-codes)

**Ready to Test?** Check out the [Commands Reference](../COMMANDS.md) for copy-paste cURL commands!

[⬆️ Back to Top](#api-reference-guide)