package com.shopping_mall_api.global.exception;

import com.shopping_mall_api.global.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GlobalShoppingMallException.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalShoppingMallException(GlobalShoppingMallException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("Business Exception: {} - {}", errorCode.getErrorCode(), e.getMessage());

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("Validation Exception: {}", errorMessage);

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(errorMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unhandled Exception: ", e);

        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.error("Internal Server Error"));
    }
}
