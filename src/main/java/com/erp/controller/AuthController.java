package com.erp.controller;

import com.erp.dto.ApiResponse;
import com.erp.dto.LoginRequest;
import com.erp.dto.LoginResponse;
import com.erp.dto.RegisterRequest;
import com.erp.service.AuthService;
import com.erp.util.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final MessageService messageService;

    /**
     * Login Endpoint
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for user: {}", request.getUsername());

        try {
            LoginResponse result = authService.login(request);
            return ResponseEntity.ok(
                ApiResponse.<LoginResponse>builder()
                    .success(true)
                    .message(messageService.get("auth.login.success"))
                    .data(result)
                    .code(200)
                    .lang(LocaleContextHolder.getLocale().getLanguage())
                    .build()
            );
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.<LoginResponse>builder()
                    .success(false)
                    .message(messageService.get("auth.login.invalid_credentials"))
                    .code(401)
                    .lang(LocaleContextHolder.getLocale().getLanguage())
                    .build()
            );
        }
    }

    /**
     * Register Endpoint
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration request for user: {}", request.getUsername());

        try {
            // Validate password confirmation
            if (!request.getPassword().equals(request.getPasswordConfirm())) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.<LoginResponse>builder()
                        .success(false)
                        .message("Passwords do not match")
                        .code(400)
                        .lang(LocaleContextHolder.getLocale().getLanguage())
                        .build()
                );
            }

            LoginResponse result = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<LoginResponse>builder()
                    .success(true)
                    .message(messageService.get("success.created"))
                    .data(result)
                    .code(201)
                    .lang(LocaleContextHolder.getLocale().getLanguage())
                    .build()
            );
        } catch (RuntimeException e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                ApiResponse.<LoginResponse>builder()
                    .success(false)
                    .message(e.getMessage())
                    .code(400)
                    .lang(LocaleContextHolder.getLocale().getLanguage())
                    .build()
            );
        }
    }

    /**
     * Get Current User (Protected)
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<String>> getCurrentUser() {
        String username = org.springframework.security.core.context.SecurityContextHolder
            .getContext().getAuthentication().getName();
        
        return ResponseEntity.ok(
            ApiResponse.<String>builder()
                .success(true)
                .message("Current user retrieved")
                .data(username)
                .code(200)
                .lang(LocaleContextHolder.getLocale().getLanguage())
                .build()
        );
    }
}