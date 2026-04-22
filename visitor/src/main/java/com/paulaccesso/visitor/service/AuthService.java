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
    public void sendLoginOtp(String empId) {
        try {
            User user = userRepository.findLoginUserByEmpId(empId)
                    .orElseThrow(() -> new RuntimeException("User not found with EmpId: " + empId));

            otpService.generateAndSendOtp(user.getEmail());
            log.info("OTP sent to: {} ({})", user.getEmail(), user.getEmpId());

        } catch (RuntimeException e) {
            log.error("Error in sendLoginOtp: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in sendLoginOtp: ", e);
            throw new RuntimeException("Failed to send OTP. Please try again later.");
        }
    }

    @Transactional
    public AuthResponse verifyOtpAndLogin(String empId, String otp) {
        User user = userRepository.findLoginUserByEmpId(empId)
                .orElseThrow(() -> new RuntimeException("User not found with EmpId: " + empId));

        boolean isValid = otpService.verifyOtp(user.getEmail(), otp);

        if (!isValid) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmpId(user.getEmpId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        userDto.setDesignation(user.getDesignation());
        userDto.setPhoto(user.getPhoto());
        userDto.setRole(user.getRole().toString());

        log.info("User logged in: {} ({}) with EmpId: {}", user.getEmail(), user.getRole(), user.getEmpId());

        return new AuthResponse(accessToken, refreshToken, "Bearer", userDto);
    }

    public void logout(String token) {
        log.info("User logged out");
    }
}