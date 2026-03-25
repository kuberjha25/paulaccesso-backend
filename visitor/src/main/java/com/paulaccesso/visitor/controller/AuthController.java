package com.paulaccesso.visitor.controller;

import com.paulaccesso.visitor.dto.AuthResponse;
import com.paulaccesso.visitor.dto.LoginRequest;
import com.paulaccesso.visitor.dto.VerifyOtpRequest;
import com.paulaccesso.visitor.dto.UserDto;
import com.paulaccesso.visitor.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(@Valid @RequestBody LoginRequest request) {
        authService.sendLoginOtp(request.getEmail());
        Map<String, String> response = new HashMap<>();
        response.put("message", "OTP sent successfully to " + request.getEmail());
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        AuthResponse response = authService.verifyOtpAndLogin(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }
}