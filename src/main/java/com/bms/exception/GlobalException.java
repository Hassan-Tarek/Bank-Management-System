package com.bms.exception;

import com.bms.dto.response.ErrorResponseDto;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(ResourceNotFoundException e) {
        log.warn("Resource not found: {}", e.getMessage());

        ErrorResponseDto error = new ErrorResponseDto(
                e.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                System.currentTimeMillis()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequest(BadRequestException e) {
        log.warn("Bad request: {}", e.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                System.currentTimeMillis()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDto> handleUnauthorized(UnauthorizedException e) {
        log.warn("Unauthorized access: {}", e.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                e.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                System.currentTimeMillis()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponseDto> handleInsufficientFunds(InsufficientFundsException e) {
        log.warn("Insufficient funds: {}", e.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                e.getMessage(),
                HttpStatus.CONFLICT.value(),
                System.currentTimeMillis()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEmailNotFound(EmailNotFoundException e) {
        log.warn("Email not found: {}", e.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                e.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                System.currentTimeMillis()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleUserAlreadyExists(UserAlreadyExistsException e) {
        log.warn("User already exists: {}", e.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                e.getMessage(),
                HttpStatus.CONFLICT.value(),
                System.currentTimeMillis()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleBadCredentials(BadCredentialsException e) {
        log.warn("Bad credentials: {}", e.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                e.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                System.currentTimeMillis()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthorizationDenied(AuthorizationDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                e.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                System.currentTimeMillis()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponseDto> handleSignature(SignatureException e) {
        log.warn("Invalid token signature: {}", e.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                e.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                System.currentTimeMillis()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneric(Exception e) {
        log.error("Unhandled exception: ", e);
        ErrorResponseDto error = new ErrorResponseDto(
                "An unexpected error occurred.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                System.currentTimeMillis()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

