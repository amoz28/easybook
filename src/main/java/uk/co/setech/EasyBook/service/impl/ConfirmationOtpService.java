package uk.co.setech.EasyBook.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.setech.EasyBook.model.ConfirmOtp;
import uk.co.setech.EasyBook.model.User;
import uk.co.setech.EasyBook.repository.ConfirmOtpRepo;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ConfirmationOtpService {

    private final ConfirmOtpRepo confirmationOtpRepository;

    public String verifyOtpByUserId(String otp, User userId) {
        var otpCheck = confirmationOtpRepository.findByOtpAndUser(otp, userId)
                .orElseThrow(() -> new IllegalCallerException("Invalid OTP Provided"));

        if (LocalDateTime.now().isAfter(otpCheck.getExpiresAt())) {
            throw new IllegalStateException("The OTP you entered has expired");
        }

        otpCheck.setConfirmedAt(LocalDateTime.now());
        otpCheck.setExpiresAt(LocalDateTime.now());
        confirmationOtpRepository.save(otpCheck);

        return "Otp Verification was Successful";
    }

    public String getOtp(User user) {
        var otpCheck = confirmationOtpRepository.findByUser(user)
                .orElseThrow(() -> new IllegalCallerException("Invalid User"));

        if (LocalDateTime.now().isAfter(otpCheck.getExpiresAt())) {
            persistOtp(otpCheck);
        }
        return otpCheck.getOtp();
    }

    private void persistOtp(ConfirmOtp otpCheck) {
        String otp = String.valueOf(new Random().nextInt(9000) + 1000);
        otpCheck.setOtp(otp);
        otpCheck.setConfirmedAt(null);
        otpCheck.setCreatedAt(LocalDateTime.now());
        otpCheck.setExpiresAt(LocalDateTime.now().plusMinutes(60 * 24));
        confirmationOtpRepository.save(otpCheck);
    }
}
