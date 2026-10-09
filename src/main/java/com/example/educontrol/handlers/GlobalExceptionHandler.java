package com.example.educontrol.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.educontrol.dto.ApiResponseDTO;
import com.example.educontrol.exceptions.InvalidOperationException;
import com.example.educontrol.exceptions.ResourceNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiResponseDTO<?> response = ApiResponseDTO.builder()
                .success(false)
                .message("Recurso não encontrado")
                .error(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ApiResponseDTO<?>> handleInvalidOperation(InvalidOperationException ex) {
        ApiResponseDTO<?> response = ApiResponseDTO.builder()
                .success(false)
                .message("Operação inválida")
                .error(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<?>> handleGenericException(Exception ex) {
        ApiResponseDTO<?> response = ApiResponseDTO.builder()
                .success(false)
                .message("Erro interno do servidor")
                .error(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
