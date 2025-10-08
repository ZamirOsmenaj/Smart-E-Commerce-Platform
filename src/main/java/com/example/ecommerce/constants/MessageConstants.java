package com.example.ecommerce.constants;

/**
 * Centralized constants for all application messages.
 * This class provides a single source of truth for success messages, error messages, and error codes.
 */
public final class MessageConstants {

    private MessageConstants() {
        // Utility class - prevent instantiation
    }

    // ========== SUCCESS MESSAGES ==========
    
    // Order Messages
    public static final String ORDER_RETRIEVED_SUCCESS = "Orders retrieved successfully";
    public static final String ORDER_CREATED_SUCCESS = "Order created successfully";
    public static final String ORDER_CANCELLED_SUCCESS = "Order cancelled successfully";
    public static final String ORDER_AVAILABLE_ACTIONS_SUCCESS = "Available actions retrieved successfully";
    public static final String ORDER_TRANSITION_CHECK_SUCCESS = "Transition check completed successfully";
    public static final String ORDER_UNDO_SUCCESS = "Command undone successfully";
    public static final String ORDER_UNDO_INFO_SUCCESS = "Undo information retrieved successfully";
    
    // Product Messages
    public static final String PRODUCT_RETRIEVED_SUCCESS = "Product retrieved successfully";
    public static final String PRODUCTS_RETRIEVED_SUCCESS = "Products retrieved successfully";
    public static final String PRODUCT_CREATED_SUCCESS = "Product created successfully";
    public static final String PRODUCT_UPDATED_SUCCESS = "Product updated successfully";
    public static final String PRODUCT_DELETED_SUCCESS = "Product deleted successfully";
    
    // Payment Messages
    public static final String PAYMENT_PROCESSED_SUCCESS = "Payment processed successfully";
    
    // Auth Messages
    public static final String USER_REGISTERED_SUCCESS = "User registered successfully";
    public static final String USER_LOGIN_SUCCESS = "User logged in successfully";

    // ========== ERROR MESSAGES ==========
    
    // Order Error Messages
    public static final String ORDER_NOT_FOUND = "Order not found";
    public static final String ORDER_CREATION_FAILED = "Order creation failed";
    public static final String ORDER_CANCELLATION_FAILED = "Order cancellation failed";
    public static final String ORDER_UPDATE_FAILED = "Order update failed";
    public static final String ORDER_ACCESS_DENIED = "Access denied to order";
    
    // Product Error Messages
    public static final String PRODUCT_NOT_FOUND = "Product not found";
    public static final String PRODUCT_CREATION_FAILED = "Product creation failed";
    public static final String PRODUCT_UPDATE_FAILED = "Product update failed";
    public static final String PRODUCT_DELETION_FAILED = "Product deletion failed";
    public static final String PRODUCT_ALREADY_EXISTS = "Product already exists";

    // Inventory Error Messages
    public static final String INVENTORY_NOT_FOUND_FOR_PRODUCT = "No inventory for product";
    public static final String INVENTORY_INSUFFICIENT_STOCK = "Insufficient stock";
    
    // Payment Error Messages
    public static final String PAYMENT_PROVIDER_UNKNOWN = "Unknown payment provider";
    public static final String CREDIT_CARD_MAXIMUM_LIMIT_FAILURE = "Credit card payment amount exceeds maximum limit";
    public static final String PAYMENT_FAILED = "Payment processing failed";
    public static final String PAYMENT_PROVIDER_INVALID = "Invalid payment provider";
    public static final String PAYMENT_INSUFFICIENT_FUNDS = "Insufficient funds";
    
    // Auth Error Messages
    public static final String REGISTRATION_FAILED = "User registration failed";
    public static final String LOGIN_FAILED = "User login failed";
    public static final String INVALID_CREDENTIALS = "Invalid credentials";
    public static final String USER_ALREADY_EXISTS = "User already exists";
    public static final String TOKEN_INVALID = "Invalid token";
    public static final String TOKEN_EXPIRED = "Token expired";
    
    // General Error Messages
    public static final String VALIDATION_FAILED = "Validation failed";
    public static final String UNAUTHORIZED_ACCESS = "Unauthorized access";
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";
    public static final String BAD_REQUEST = "Bad request";
    public static final String RESOURCE_NOT_FOUND = "Resource not found";
    public static final String ACCESS_DENIED = "Access denied";
    
    // Command Messages
    public static final String COMMAND_EXECUTED_SUCCESS = "Command executed successfully";
    public static final String COMMAND_EXECUTION_FAILED = "Command execution failed";
    public static final String COMMAND_UNDO_NOT_SUPPORTED = "Command undo is not supported";
    public static final String COMMAND_UNDO_NO_ORDER = "Cannot undo: No order was created";
    
    // Security Messages
    public static final String USER_DOES_NOT_OWN_ORDER = "User does not own this order";
    public static final String ORDER_ACCESS_DENIED_DETAILED = "Access denied: You do not own this order";
    public static final String AUTHENTICATION_REQUIRED = "Authentication required";
    public static final String AUTHENTICATION_FAILED = "Authentication failed";
    public static final String MISSING_AUTH_TOKEN = "Missing auth token in SOAP header";
    public static final String EMPTY_AUTH_TOKEN = "Empty auth token";
    
    // Validation Messages
    public static final String ORDER_VALIDATION_FAILED = "Order validation failed";
    public static final String ORDER_VALIDATION_SUCCESS = "Order validation chain completed successfully";
    public static final String CANNOT_CANCEL_ORDER = "Cannot cancel order";
    public static final String ORDER_MUST_BE_PENDING = "Order must be in PENDING status to process payment";
    public static final String ORDER_TOTAL_INVALID = "Order total must be greater than zero";
    public static final String ORDER_CANNOT_BE_NULL = "Order cannot be null";
    
    // SOAP Messages
    public static final String ORDER_STATUS_UPDATED_SUCCESS = "Order status updated successfully";
    public static final String ORDER_STATUS_UPDATE_FAILED = "Failed to update order status";
    public static final String ORDER_CREATION_SOAP_FAILED = "Failed to create order";
    public static final String ORDER_ACCESS_SOAP_FAILED = "Order access failed";
    
    // Service Messages
    public static final String USER_NOT_FOUND = "User not found!";
    public static final String JWT_INITIALIZED_SUCCESS = "JwtService initialized successfully";
    
    // Undo Messages
    public static final String ORDER_CANCELLATION_UNDO_NOT_SUPPORTED = "Undoing order cancellation is not supported for business reasons";
    public static final String ORDER_CREATION_UNDONE_SUCCESS = "Order creation undone successfully";
    public static final String ORDER_CREATION_UNDO_FAILED = "Failed to undo order creation";

    // ========== ERROR CODES ==========
    
    // Order Error Codes
    public static final String ORDER_CREATION_FAILED_CODE = "ORDER_CREATION_FAILED";
    public static final String ORDER_CANCELLATION_FAILED_CODE = "ORDER_CANCELLATION_FAILED";
    public static final String ORDER_NOT_FOUND_CODE = "ORDER_NOT_FOUND";
    public static final String ORDER_ACCESS_DENIED_CODE = "ORDER_ACCESS_DENIED";
    public static final String UNDO_FAILED_CODE = "UNDO_FAILED";
    
    // Product Error Codes
    public static final String PRODUCT_CREATION_FAILED_CODE = "PRODUCT_CREATION_FAILED";
    public static final String PRODUCT_UPDATE_FAILED_CODE = "PRODUCT_UPDATE_FAILED";
    public static final String PRODUCT_DELETION_FAILED_CODE = "PRODUCT_DELETION_FAILED";
    public static final String PRODUCT_NOT_FOUND_CODE = "PRODUCT_NOT_FOUND";
    
    // Payment Error Codes
    public static final String PAYMENT_FAILED_CODE = "PAYMENT_FAILED";
    public static final String PAYMENT_PROVIDER_INVALID_CODE = "PAYMENT_PROVIDER_INVALID";
    
    // Auth Error Codes
    public static final String REGISTRATION_FAILED_CODE = "REGISTRATION_FAILED";
    public static final String LOGIN_FAILED_CODE = "LOGIN_FAILED";
    public static final String INVALID_CREDENTIALS_CODE = "INVALID_CREDENTIALS";
    public static final String TOKEN_INVALID_CODE = "TOKEN_INVALID";
    
    // General Error Codes
    public static final String VALIDATION_FAILED_CODE = "VALIDATION_FAILED";
    public static final String UNAUTHORIZED_ACCESS_CODE = "UNAUTHORIZED_ACCESS";
    public static final String INTERNAL_SERVER_ERROR_CODE = "INTERNAL_SERVER_ERROR";
}