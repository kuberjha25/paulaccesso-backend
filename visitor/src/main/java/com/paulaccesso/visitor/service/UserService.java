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
        // Check if empId is unique
        if (userRepository.existsByEmpId(request.getEmpId())) {
            throw new RuntimeException("Employee ID already exists: " + request.getEmpId());
        }

        Role role = Role.valueOf(request.getRole());

        // For ADMIN or RECEPTIONIST - check if email already exists as login user
        if (role == Role.ADMIN || role == Role.RECEPTIONIST) {
            boolean exists = userRepository.existsLoginUserByEmail(request.getEmail());
            if (exists) {
                throw new RuntimeException("User with email " + request.getEmail() +
                        " already exists as ADMIN or RECEPTIONIST. Duplicate login users not allowed.");
            }
        }

        User user = new User();
        user.setEmpId(request.getEmpId());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setDesignation(request.getDesignation());
        user.setPhoto(request.getPhoto());
        user.setRole(role);
        user.setActive(true);

        User saved = userRepository.save(user);
        log.info("User created: {} ({}) with EmpId: {}", saved.getEmail(), saved.getRole(), saved.getEmpId());

        return mapToResponse(saved);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role newRole = Role.valueOf(request.getRole());

        // If empId is being changed, check uniqueness
        if (!user.getEmpId().equals(request.getEmpId()) && userRepository.existsByEmpId(request.getEmpId())) {
            throw new RuntimeException("Employee ID already exists: " + request.getEmpId());
        }

        // If role is changing to ADMIN/RECEPTIONIST, check email uniqueness
        if ((newRole == Role.ADMIN || newRole == Role.RECEPTIONIST) &&
                !user.getEmail().equals(request.getEmail())) {

            boolean exists = userRepository.existsLoginUserByEmail(request.getEmail());
            if (exists) {
                throw new RuntimeException("User with email " + request.getEmail() +
                        " already exists as ADMIN or RECEPTIONIST");
            }
        }

        user.setEmpId(request.getEmpId());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setDesignation(request.getDesignation());
        if (request.getPhoto() != null) {
            user.setPhoto(request.getPhoto());
        }
        user.setRole(newRole);

        User updated = userRepository.save(user);
        log.info("User updated: {} ({})", updated.getEmail(), updated.getRole());

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
        response.setEmpId(user.getEmpId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setMobile(user.getMobile());
        response.setDesignation(user.getDesignation());
        response.setPhoto(user.getPhoto());
        response.setRole(user.getRole().toString());
        response.setActive(user.isActive());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}