package com.paulaccesso.visitor.controller;

import com.paulaccesso.visitor.dto.UserRequest;
import com.paulaccesso.visitor.dto.UserResponse;
import com.paulaccesso.visitor.service.UserService;
import com.paulaccesso.visitor.security.JwtService;
import com.paulaccesso.visitor.entity.Role;
import com.paulaccesso.visitor.entity.User;
import com.paulaccesso.visitor.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody UserRequest request) {
        
        String jwt = token.substring(7);
        Long userId = jwtService.getUserIdFromToken(jwt);
        User currentUser = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only admin can create users");
        }
        
        UserResponse response = userService.createUser(request);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        
        String jwt = token.substring(7);
        Long userId = jwtService.getUserIdFromToken(jwt);
        User currentUser = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only admin can update users");
        }
        
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        
        String jwt = token.substring(7);
        Long userId = jwtService.getUserIdFromToken(jwt);
        User currentUser = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only admin can delete users");
        }
        
        userService.deleteUser(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "User deleted successfully");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestHeader("Authorization") String token) {
        
        String jwt = token.substring(7);
        Long userId = jwtService.getUserIdFromToken(jwt);
        User currentUser = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only admin can view all users");
        }
        
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/employees")
    public ResponseEntity<List<UserResponse>> getEmployees() {
        List<UserResponse> employees = userService.getEmployees();
        return ResponseEntity.ok(employees);
    }
    
    @GetMapping("/receptionists")
    public ResponseEntity<List<UserResponse>> getReceptionists() {
        List<UserResponse> receptionists = userService.getReceptionists();
        return ResponseEntity.ok(receptionists);
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            @RequestHeader("Authorization") String token) {
        
        String jwt = token.substring(7);
        Long userId = jwtService.getUserIdFromToken(jwt);
        UserResponse response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }
}