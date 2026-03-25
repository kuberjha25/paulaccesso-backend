package com.paulaccesso.visitor.service;

import com.paulaccesso.visitor.dto.AuthResponse;
import com.paulaccesso.visitor.dto.UserDto;
import com.paulaccesso.visitor.entity.Role;
import com.paulaccesso.visitor.entity.User;
import com.paulaccesso.visitor.repository.UserRepository;
import com.paulaccesso.visitor.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    private final OtpService otpService;
    private final JwtService jwtService;
    
    @Transactional
    public void sendLoginOtp(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.RECEPTIONIST) {
            throw new RuntimeException("Access denied. Only admin and receptionist can login.");
        }
        
        otpService.generateAndSendOtp(email);
    }
    
    @Transactional
    public AuthResponse verifyOtpAndLogin(String email, String otp) {
        boolean isValid = otpService.verifyOtp(email, otp);
        
        if (!isValid) {
            throw new RuntimeException("Invalid or expired OTP");
        }
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.RECEPTIONIST) {
            throw new RuntimeException("Access denied");
        }
        
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        userDto.setDesignation(user.getDesignation());
        userDto.setPhoto(user.getPhoto());
        userDto.setRole(user.getRole().toString());
        
        log.info("User logged in: {} ({})", email, user.getRole());
        
        return new AuthResponse(accessToken, refreshToken, "Bearer", userDto);
    }
    
    public void logout(String token) {
        log.info("User logged out");
    }
}