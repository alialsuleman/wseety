package com.example.wseety ;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;


import java.util.Collections;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
    private LocalDateTime timestamp;
    private int status;

    // =========================================================================
    //  1. Success Responses (2xx)
    // =========================================================================

    public static <T> ApiResponse<T> ok(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .status(200)
                .timestamp(LocalDateTime.now())
                .build();
    }
    public static <T> ApiResponse<T> ok(T data) {
        return ok("Operation completed successfully.", data);
    }


    public static <T> ApiResponse<T> accepted(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .status(202)
                .timestamp(LocalDateTime.now())
                .build();
    }


    public static <T> ApiResponse<T> created(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .status(201)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // =========================================================================
    //  2. Client Error Responses (4xx)
    // =========================================================================

    public static <T> ApiResponse<T> badRequest(String message, T body) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errors(null)
                .data(body)
                .status(400)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> badRequest(String message) {
        return badRequest(message, null);
    }

    public static <T> ApiResponse<T> unauthorized(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .status(401)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 403 Forbidden: User is authenticated but lacks permission for the resource.
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .status(403)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .status(404)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 409 Conflict: Resource state conflict, e.g., Email already exists.
     */
    public static <T> ApiResponse<T> conflict(String message, String error) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errors(Collections.singletonList(error))
                .status(409)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 422 Unprocessable Entity: Great for validation errors from Spring's BindingResult.
     */
    public static <T> ApiResponse<T> unprocessableEntity(String message, List<String> errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .status(422)
                .timestamp(LocalDateTime.now())
                .build();
    }



    public static <T> ApiResponse<T> tooManyRequests(String message, T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(data)
                .status(429)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // =========================================================================
    //  3. Server Error Responses (5xx) & Custom
    // =========================================================================

    public static <T> ApiResponse<T> internalServerError(String message, String error) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errors(Collections.singletonList(error))
                .status(500)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Wildcard / Custom method for any other HTTP status code not explicitly defined.
     */
    public static <T> ApiResponse<T> custom(boolean success, int status, String message, T data, List<String> errors) {
        return ApiResponse.<T>builder()
                .success(success)
                .status(status)
                .message(message)
                .data(data)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> badGateway(String message, T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(data)
                .status(502)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> gatewayTimeout(String message, T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(data)
                .status(504)
                .timestamp(LocalDateTime.now())
                .build();
    }
    public static <T> ApiResponse<T> internalServerError(String message, T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(data)
                .status(500)
                .timestamp(LocalDateTime.now())
                .build();
    }

}