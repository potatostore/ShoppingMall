package com.shopping_mall_api.global.exception;

import com.shopping_mall_api.global.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected @Nullable ResponseEntity<Object> handleExceptionInternal(
            Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        if(body instanceof ProblemDetail){
            body = ApiResponse.error(
                    ((ProblemDetail) body).getDetail()
            );
        }
        else if (body == null){
            if(ex instanceof ErrorResponse){
                body = ApiResponse.error(
                        ((ErrorResponse) ex).getBody().getDetail()
                );
            }
            else{
                HttpStatus status = HttpStatus.resolve(statusCode.value());
                String message = (status != null)
                        ? status.getReasonPhrase()
                        : "Unexpected Error";

                body = ApiResponse.error(
                        message
                );
            }
        }

        if(statusCode.is4xxClientError()){
            log.warn(ex.getClass().getSimpleName() + " " + request.getDescription(false));
        }
        else{
            log.error(ex.getMessage(), ex);
        }

        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        FieldError error = ex.getBindingResult().getFieldError();

        Object body = ApiResponse.error(
                (error != null) ? error.getDefaultMessage() : "Invalid Argument"
        );

        return this.handleExceptionInternal(ex, body, headers, status, request);
    }

    @ExceptionHandler(GlobalShoppingMallException.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalShoppingMallException(GlobalShoppingMallException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("Business Exception: {} - {}", errorCode.getErrorCode(), e.getMessage());

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unhandled Exception: ", e);

        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.error("Internal Server Error"));
    }
}
