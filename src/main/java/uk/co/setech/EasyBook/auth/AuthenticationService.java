package uk.co.setech.EasyBook.auth;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uk.co.setech.EasyBook.auth.token.ConfirmationOtp;
import uk.co.setech.EasyBook.auth.token.ConfirmationOtpRepository;
import uk.co.setech.EasyBook.auth.token.ConfirmationOtpService;
import uk.co.setech.EasyBook.config.JwtService;
import uk.co.setech.EasyBook.email.EmailService;
import uk.co.setech.EasyBook.repository.UserRepo;
import uk.co.setech.EasyBook.user.Role;
import uk.co.setech.EasyBook.user.User;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final String USER_NOT_FOUND = "User with email: %s Not Found";
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final EmailService emailService;
    private final ConfirmationOtpService confirmationOtpService;
    private final ConfirmationOtpRepository confirmationOtpRepository;

    public GeneralResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepo.save(user);

        String otp = String.valueOf(new Random().nextInt(9000) + 1000);

        var confirmationOtp = ConfirmationOtp.builder()
                .otp(otp)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(60*24))
                .user(user)
                .build();

        confirmationOtpRepository.save(confirmationOtp);

        String subject = "Account Verification OTP";
        String message = "Please enter the OTP to complete you email verification process "+otp;
        emailService.send(user.getFirstName(), user.getEmail(), message, subject );
//       @TODO sendMail(email, message, subject );
        var jwtToken = jwtService.generateToken(user);

        return GeneralResponse
                .builder()
                .message("Account successfuly created, an OTP has been sent to your account for verification")
                .build();
    }

    public AuthenticationResponse authentication(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword())
        );
        var user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(()->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, request.getEmail())));

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .firstname(user.getFirstName())
                .lastname(user.getLastName())
                .email(request.getEmail())
                .token(jwtToken)
                .build();
    }

    public GeneralResponse verifyOtp(VerificationRequest request) {
        var user = userRepo.findByEmail(request.email())
                .orElseThrow(()->new UsernameNotFoundException(String.format(USER_NOT_FOUND, request.email())));

        String confirmationMsg = confirmationOtpService.verifyOtpByUserId(request.otp(), user);
        user.setEnabled(Boolean.TRUE);
        userRepo.save(user);

        return GeneralResponse.builder()
                .message(confirmationMsg)
                .build();
    }

    public GeneralResponse resendOtp(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));

        var otp = confirmationOtpService.getOtp(user);
        String subject = "Resend OTP";
        String message = "Here is the OTP you requested for to complete the process "+otp;
        emailService.send(user.getFirstName(), email, message, subject );
        return GeneralResponse.builder()
                .message("OTP has been resent check your email")
                .build();
    }

    public GeneralResponse forgotPassword(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));

        var otp = confirmationOtpService.getOtp(user);
        String subject = "Reset Password - OTP Verification";
        String message = "You are about to reset your password, use this OTP to complete the reset process "+otp;
        emailService.send(user.getFirstName(), user.getEmail(), message, subject);

        //       @TODO sendMail(email, message, subject );
        return GeneralResponse.builder()
                .message("An otp has been sent to your email for verification")
                .build();
    }

    public AuthenticationResponse resetPassword(AuthenticationRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(()->new UsernameNotFoundException(String.format(USER_NOT_FOUND, request.getEmail())));

        var confOtp = confirmationOtpRepository.findByUser(user)
                .orElseThrow(()-> new IllegalStateException("Invalid UserId"));

        if(LocalDateTime.now().isAfter(confOtp.getConfirmedAt().plusMinutes(5))){
            throw new IllegalStateException("Request Timed Out please try again");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepo.save(user);
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .firstname(user.getFirstName())
                .lastname(user.getLastName())
                .email(user.getEmail())
                .token(jwtToken)
                .build();
    }

}
