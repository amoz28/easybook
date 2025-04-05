package uk.co.setech.easybook.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.co.setech.easybook.exception.CustomException;
import uk.co.setech.easybook.model.ConfirmOtp;
import uk.co.setech.easybook.model.User;
import uk.co.setech.easybook.repository.ConfirmOtpRepo;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ConfirmOtpService {

    private final ConfirmOtpRepo confirmationOtpRepository;

    public String verifyOtpByUserId(String otp, User userId) {
        var otpCheck = confirmationOtpRepository.findByOtpAndUser(otp, userId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid OTP Provided"));

        if (LocalDateTime.now().isAfter(otpCheck.getExpiresAt())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "The OTP you entered has expired");
        }

        otpCheck.setConfirmedAt(LocalDateTime.now());
        otpCheck.setExpiresAt(LocalDateTime.now());
        confirmationOtpRepository.save(otpCheck);

        return "Otp Verification was Successful";
    }

    public String getOtp(User user) {
        var otpCheck = confirmationOtpRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Invalid User"));

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
        otpCheck.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        confirmationOtpRepository.save(otpCheck);
    }
}
