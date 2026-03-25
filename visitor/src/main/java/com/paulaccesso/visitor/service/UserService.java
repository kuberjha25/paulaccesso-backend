package com.paulaccesso.visitor.service;

import com.paulaccesso.visitor.dto.UserRequest;
import com.paulaccesso.visitor.dto.UserResponse;
import com.paulaccesso.visitor.entity.Role;
import com.paulaccesso.visitor.entity.User;
import com.paulaccesso.visitor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    
    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setDesignation(request.getDesignation());
        user.setPhoto(request.getPhoto());
        user.setRole(Role.valueOf(request.getRole()));
        user.setActive(true);
        
        User saved = userRepository.save(user);
        log.info("User created: {} as {}", saved.getEmail(), saved.getRole());
        
        return mapToResponse(saved);
    }
    
    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setName(request.getName());
        user.setDesignation(request.getDesignation());
        if (request.getPhoto() != null) {
            user.setPhoto(request.getPhoto());
        }
        user.setRole(Role.valueOf(request.getRole()));
        
        User updated = userRepository.save(user);
        log.info("User updated: {}", updated.getEmail());
        
        return mapToResponse(updated);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Cannot delete admin user");
        }
        
        userRepository.delete(user);
        log.info("User deleted: {}", user.getEmail());
    }
    
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public List<UserResponse> getEmployees() {
        return userRepository.findByRole(Role.EMPLOYEE).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public List<UserResponse> getReceptionists() {
        return userRepository.findByRole(Role.RECEPTIONIST).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }
    
    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setDesignation(user.getDesignation());
        response.setPhoto(user.getPhoto());
        response.setRole(user.getRole().toString());
        response.setActive(user.isActive());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}