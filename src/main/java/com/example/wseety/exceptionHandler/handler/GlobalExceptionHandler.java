package com.example.wseety.exceptionHandler.handler;


import com.example.wseety.ApiResponse;
import com.example.wseety.exceptionHandler.entity.ErrorDetails;
import com.example.wseety.exceptionHandler.exception.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {





    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<?>> handleBaseException(BaseException ex, HttpServletRequest request) {

        // log section
        ErrorDetails errorDetails = createErrorDetails(ex, request ) ;


        // api response section
        HashMap<String , String> errorData = new HashMap<String , String>() ;
        errorData.put("traceId" , errorDetails.getTraceId() );

        ApiResponse<?> response = ApiResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .data(errorData)
                .errors(List.of(ex.getMessage()))
                .timestamp(LocalDateTime.now())
                .status(ex.getStatus().value())
                .build();



        return ResponseEntity
                .status(ex.getStatus())
                .body(response);


    }





    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleDtoValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        //
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        //
        ApiResponse<Map<String, String>> response = ApiResponse.badRequest("Validation failed for the request body", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleParameterValidationErrors(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(violation -> {
            //
            String propertyPath = violation.getPropertyPath().toString();
            //
            String paramName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);

            errors.put(paramName, violation.getMessage());
        });

        ApiResponse<Map<String, String>> response = ApiResponse.badRequest("Validation failed for the URL parameters", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);


    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(
            Exception ex,
            HttpServletRequest request
    ) {


        // log section
        ErrorDetails errorDetails = createErrorDetails(ex, request ) ;



        // api response section
        HashMap<String , String> errorData = new HashMap<String , String>() ;
        errorData.put("traceId" , errorDetails.getTraceId() );


        ApiResponse<?> response = ApiResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .data(errorData)
                .errors(List.of("Unexpected error occurred"))
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();



        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }



    private ErrorDetails createErrorDetails (
            Exception ex,
            HttpServletRequest request )
    {

        String traceId = UUID.randomUUID().toString().substring(0, 8);


        StackTraceElement element = ex.getStackTrace()[0];
        return ErrorDetails.builder()
                .traceId(traceId)
                .exception(ex.getClass().getSimpleName())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .className(element.getClassName())
                .methodName(element.getMethodName())
                .lineNumber(element.getLineNumber())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
