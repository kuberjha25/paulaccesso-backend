package com.paulaccesso.visitor.service;

import com.paulaccesso.visitor.entity.Otp;
import com.paulaccesso.visitor.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {
    
    private final OtpRepository otpRepository;
    private final EmailService emailService;
    
    @Value("${otp.expiration-minutes:5}")
    private int expirationMinutes;
    
    @Value("${otp.length:6}")
    private int otpLength;
    
    private final SecureRandom secureRandom = new SecureRandom();
    
    @Transactional
    public String generateAndSendOtp(String email) {
        otpRepository.deleteExpiredOtps(LocalDateTime.now());
        
        String otp = generateOtp();
        
        Otp otpEntity = new Otp();
        otpEntity.setEmail(email);
        otpEntity.setOtp(otp);
        otpEntity.setExpiresAt(LocalDateTime.now().plusMinutes(expirationMinutes));
        otpEntity.setUsed(false);
        otpRepository.save(otpEntity);
        
        emailService.sendOtpEmail(email, otp);
        
        log.info("OTP generated and sent to: {}", email);
        return otp;
    }
    
    @Transactional
    public boolean verifyOtp(String email, String otp) {
        Optional<Otp> otpOpt = otpRepository.findByEmailAndOtpAndUsedFalse(email, otp);
        
        if (otpOpt.isEmpty()) {
            log.warn("Invalid OTP attempt for: {}", email);
            return false;
        }
        
        Otp otpEntity = otpOpt.get();
        
        if (otpEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Expired OTP attempt for: {}", email);
            return false;
        }
        
        otpRepository.markOtpAsUsed(email, otp);
        log.info("OTP verified successfully for: {}", email);
        return true;
    }
    
    private String generateOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(secureRandom.nextInt(10));
        }
        return otp.toString();
    }
}