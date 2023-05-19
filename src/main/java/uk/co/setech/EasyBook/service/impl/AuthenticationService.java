package uk.co.setech.EasyBook.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uk.co.setech.EasyBook.commons.security.JwtService;
import uk.co.setech.EasyBook.dto.*;
import uk.co.setech.EasyBook.email.EmailService;
import uk.co.setech.EasyBook.enums.Role;
import uk.co.setech.EasyBook.model.ConfirmOtp;
import uk.co.setech.EasyBook.model.User;
import uk.co.setech.EasyBook.repository.ConfirmOtpRepo;
import uk.co.setech.EasyBook.repository.UserRepo;
import uk.co.setech.EasyBook.service.InvoiceService;

import java.time.LocalDate;
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

    private final InvoiceService invoiceService;

    private final EmailService emailService;
    private final ConfirmationOtpService confirmationOtpService;
    private final ConfirmOtpRepo confirmationOtpRepository;

    public GeneralResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepo.save(user);

        String otp = handleOtp(user);

        String subject = "Account Verification OTP";
        String message = "Please enter the OTP to complete you email verification process " + otp;
        emailService.send(user.getFirstName(), user.getEmail(), message, subject);
//       @TODO sendMail(email, message, subject );

        return GeneralResponse
                .builder()
                .message("Account successfuly created, an OTP has been sent to your account for verification")
                .build();
    }

    private String handleOtp(User user) {

        String otp = String.valueOf(new Random().nextInt(9000) + 1000);

        var confirmationOtp = ConfirmOtp.builder()
                .otp(otp)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(60 * 24))
                .user(user)
                .build();

        confirmationOtpRepository.save(confirmationOtp);
        return otp;
    }

    public AuthenticationResponse authentication(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword())
        );

        var user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, request.getEmail())));

        var totalOverdueInvoices = invoiceService.getInvoiceDtos(request.getEmail()).stream()
                .filter(invoiceDto -> invoiceDto.isInvoicePaid()
                        && invoiceDto.getDuedate().isAfter(LocalDate.now()))
                .mapToDouble(InvoiceDto::getTotal)
                .sum();

        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .firstname(user.getFirstName())
                .lastname(user.getLastName())
                .email(request.getEmail())
                .overdueInvoice(totalOverdueInvoices)
                .token(jwtToken)
                .build();
    }

    public GeneralResponse verifyOtp(VerificationRequest request) {
        var user = userRepo.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, request.email())));

        String confirmationMsg = confirmationOtpService.verifyOtpByUserId(request.otp(), user);
        user.setEnabled(Boolean.TRUE);
        userRepo.save(user);

        return GeneralResponse.builder()
                .message(confirmationMsg)
                .build();
    }

    public GeneralResponse resendOtp(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));

        var otp = confirmationOtpService.getOtp(user);
        String subject = "Resend OTP";
        String message = "Here is the OTP you requested for to complete the process " + otp;
        emailService.send(user.getFirstName(), email, message, subject);
        return GeneralResponse.builder()
                .message("OTP has been resent check your email")
                .build();
    }

    public GeneralResponse forgotPassword(String email) {
        System.out.println("=============");
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));
        System.out.println("User found "+user.getLastName());

        String otp = String.valueOf(new Random().nextInt(9000) + 1000);

        var confOtp = confirmationOtpRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Invalid UserId"));
        confOtp.setOtp(otp);
        confOtp.setCreatedAt(LocalDateTime.now());
        confOtp.setExpiresAt(LocalDateTime.now().plusMinutes(60 * 24));
        confOtp.setUser(user);
        confirmationOtpRepository.save(confOtp);

//        var otp = handleOtp(user);
        System.out.println("OTP == "+otp);
        String subject = "Reset Password - OTP Verification";
        String message = "You are about to reset your password, use this OTP to complete the reset process " + otp;
        emailService.send(user.getFirstName(), user.getEmail(), message, subject);

        //       @TODO sendMail(email, message, subject );
        return GeneralResponse.builder()
                .message("An otp has been sent to your email for verification")
                .build();
    }

    public AuthenticationResponse resetPassword(AuthenticationRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, request.getEmail())));
        System.out.println("User found "+user.getEmail());
//      @TODO ENSURE USER ARE NOT ABLE TO RESET PASSWORD TWICE WITHOUT CALLING FORGOT PASSWORD TWICE
        var confOtp = confirmationOtpRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Invalid UserId"));
        System.out.println("User Confirmed "+confOtp);

        if (LocalDateTime.now().isAfter(confOtp.getConfirmedAt().plusMinutes(5))) {
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
