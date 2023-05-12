package uk.co.setech.EasyBook.auth.token;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.setech.EasyBook.auth.GeneralResponse;
import uk.co.setech.EasyBook.auth.VerificationRequest;
import uk.co.setech.EasyBook.user.User;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ConfirmationOtpService {

    private final ConfirmationOtpRepository confirmationOtpRepository;

    public String verifyOtpByUserId(String otp, User userId) {
        var otpCheck = confirmationOtpRepository.findByOtpAndUser(otp, userId)
                .orElseThrow(()->new IllegalCallerException("Invalid OTP"));

        if (LocalDateTime.now().isAfter(otpCheck.getExpiresAt())){
            throw new IllegalStateException("OTP Expired");
        }

        otpCheck.setConfirmedAt(LocalDateTime.now());
        otpCheck.setExpiresAt(LocalDateTime.now());
        confirmationOtpRepository.save(otpCheck);

        return "Otp Verification was Successful";
    }

    public static void main(String[] args) {
        System.out.println(Thread.activeCount());
        System.out.println(Runtime.getRuntime().availableProcessors());
    }
    public String getOtp(User userId) {
        var otpCheck = confirmationOtpRepository.findByUser(userId)
                .orElseThrow(()->new IllegalCallerException("Invalid User Id"));

        if (LocalDateTime.now().isAfter(otpCheck.getExpiresAt())){
            String otp = String.valueOf(new Random().nextInt(9000) + 1000);
            otpCheck.setOtp(otp);
            otpCheck.setConfirmedAt(null);
            otpCheck.setCreatedAt(LocalDateTime.now());
            otpCheck.setExpiresAt(LocalDateTime.now().plusMinutes(60*24));
            confirmationOtpRepository.save(otpCheck);
        }
        return otpCheck.getOtp();
    }
}