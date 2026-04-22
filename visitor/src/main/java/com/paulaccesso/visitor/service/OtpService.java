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
import java.util.Arrays;
import java.util.List;
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

    // Hardcoded OTP for test accounts
    private static final String TEST_OTP = "246810";
    private static final List<String> TEST_EMAILS = Arrays.asList("test@pml.com", "kuber@pml.com", "admin@pml.com");

    @Transactional
    public String generateAndSendOtp(String email) {
        otpRepository.deleteExpiredOtps(LocalDateTime.now());

        String otp;

        // Check if this is a test account
        if (TEST_EMAILS.contains(email)) {
            otp = TEST_OTP; // Use hardcoded OTP for test accounts
            log.info("Using hardcoded OTP {} for test account: {}", TEST_OTP, email);
        } else {
            otp = generateOtp();
            log.info("Generated random OTP for: {}", email);
        }

        Otp otpEntity = new Otp();
        otpEntity.setEmail(email);
        otpEntity.setOtp(otp);
        otpEntity.setExpiresAt(LocalDateTime.now().plusMinutes(expirationMinutes));
        otpEntity.setUsed(false);
        otpRepository.save(otpEntity);

        // Send email (with error handling for test accounts)
        try {
            emailService.sendOtpEmail(email, otp);
            log.info("OTP email sent successfully to: {}", email);
        } catch (Exception e) {
            log.error("Email failed but continuing flow for: {}", email);
            // For test accounts, print OTP in logs
            if (TEST_EMAILS.contains(email)) {
                log.info("=== TEST OTP FOR {}: {} ===", email, otp);
            }
        }

        log.info("OTP generated and sent to: {}", email);
        return otp;
    }

    @Transactional
    public boolean verifyOtp(String email, String otp) {
        log.info("Verifying OTP for email: {}, provided OTP: {}", email, otp);

        // FIRST: Check for hardcoded test OTP
        if (TEST_EMAILS.contains(email) && TEST_OTP.equals(otp)) {
            log.info("Hardcoded OTP matched for test account: {}", email);

            // Mark OTP as used in database to maintain consistency
            try {
                otpRepository.markOtpAsUsed(email, otp);
            } catch (Exception e) {
                log.debug("No existing OTP to mark as used for test account");
            }

            return true;
        }

        // SECOND: Check database for regular users
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