package nl.hu.bep.presentation.dto;

import java.time.Instant;

public record ApiResponse<T>(
    String status,
    T data,
    Long timestamp,
    String message
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", data, Instant.now().toEpochMilli(), null);
    }
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>("success", data, Instant.now().toEpochMilli(), message);
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("error", null, Instant.now().toEpochMilli(), message);
    }
    
    public static <T> ApiResponse<T> error(T data, String message) {
        return new ApiResponse<>("error", data, Instant.now().toEpochMilli(), message);
    }
} 