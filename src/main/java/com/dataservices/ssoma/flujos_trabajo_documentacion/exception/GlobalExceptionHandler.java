package com.dataservices.ssoma.flujos_trabajo_documentacion.exception;

import com.dataservices.ssoma.flujos_trabajo_documentacion.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        log.error("Recurso no encontrado: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), "RESOURCE_NOT_FOUND");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicatedResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicatedResourceException(
            DuplicatedResourceException ex, WebRequest request) {

        log.error("Recurso duplicado: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), "DUPLICATED_RESOURCE");
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {

        log.error("Error de autenticación: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), "AUTHENTICATION_ERROR");
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(
            BusinessException ex, WebRequest request) {

        log.error("Error de negocio: {} - Código: {}", ex.getMessage(), ex.getErrorCode());

        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {

        log.error("Error de validación: {}", ex.getMessage());

        Map<String, Object> errorDetails = new HashMap<>();
        List<String> fieldErrors = new ArrayList<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.add(error.getField() + ": " + error.getDefaultMessage());
        }

        errorDetails.put("fieldErrors", fieldErrors);
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        errorDetails.put("timestamp", LocalDateTime.now());

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(false)
                .message("Los datos proporcionados no son válidos")
                .errorCode("VALIDATION_ERROR")
                .data(errorDetails)
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        log.error("Argumento inválido: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), "INVALID_ARGUMENT");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(
            Exception ex, WebRequest request) {

        log.error("Error interno del servidor: ", ex);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("error", "Internal Server Error");

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(false)
                .message("Ha ocurrido un error interno en el servidor")
                .errorCode("INTERNAL_SERVER_ERROR")
                .data(errorDetails)
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
