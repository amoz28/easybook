package uk.co.setech.easybook.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uk.co.setech.easybook.commons.security.JwtService;
import uk.co.setech.easybook.dto.AuthenticationRequest;
import uk.co.setech.easybook.dto.AuthenticationResponse;
import uk.co.setech.easybook.dto.GeneralResponse;
import uk.co.setech.easybook.dto.InvoiceDto;
import uk.co.setech.easybook.dto.InvoiceSummary;
import uk.co.setech.easybook.dto.RegisterRequest;
import uk.co.setech.easybook.dto.VerificationRequest;
import uk.co.setech.easybook.email.EmailSender;
import uk.co.setech.easybook.email.EmailService;
import uk.co.setech.easybook.enums.InvoiceType;
import uk.co.setech.easybook.enums.Role;
import uk.co.setech.easybook.exception.CustomException;
import uk.co.setech.easybook.model.ConfirmOtp;
import uk.co.setech.easybook.model.User;
import uk.co.setech.easybook.repository.ConfirmOtpRepo;
import uk.co.setech.easybook.repository.UserRepo;
import uk.co.setech.easybook.service.InvoiceService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final String USER_NOT_FOUND = "User with email: %s Not Found";
    private static final String USER_ALREADY_EXIST = "User with email: %s Already Exists";
    private static final String CONTINUE_REGISTRSTION = "User with email: %s has an incomplete Registration";
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final InvoiceService invoiceService;

    private final EmailSender emailService;
    private final ConfirmOtpService confirmOtpService;
    private final ConfirmOtpRepo confirmOtpRepo;
    private static final Map<String, String> otpCache = new HashMap<>();


    public GeneralResponse register(RegisterRequest request) {
        request.setEmail(request.getEmail().toLowerCase());
        var userObj = userRepo.findByEmail(request.getEmail());
        if (userObj.isPresent() && userObj.get().isEnabled()) {
            throw new CustomException(HttpStatus.FORBIDDEN, String.format(USER_ALREADY_EXIST, request.getEmail()));
        } else if (userObj.isPresent() && !userObj.get().isEnabled()) {
            throw new CustomException(HttpStatus.CONFLICT, String.format(CONTINUE_REGISTRSTION, request.getEmail()));
        }

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

        return GeneralResponse
                .builder()
                .status(HttpStatus.CREATED.value())
                .message("Account successfully created, an OTP has been sent to your account for verification")
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

        confirmOtpRepo.save(confirmationOtp);
        return otp;
    }

    public AuthenticationResponse authentication(AuthenticationRequest request) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        var user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND, request.getEmail())));

        var allInvoices = invoiceService.getOverdueAndPaidInvoice(user.getId(), InvoiceType.INVOICE);

//        var recentInvoice = invoiceService.getAllInvoicesWithSize(0,10, "INVOICE", "ESTIMATE");
        var recentInvoice = invoiceService.getOverdueInvoicesWithSize(0,10, "INVOICE", "ESTIMATE");

        var jwtToken = jwtService.generateToken(user);
        var shortCutList = new ArrayList<InvoiceSummary>();

        shortCutList.add(
                InvoiceSummary.builder()
                        .title("Overdue Invoices")
                        .image("wallet")
                        .amount(allInvoices.getOverdueInvoiceTotal())
                        .build());

        shortCutList.add(
                InvoiceSummary.builder()
                        .title("Paid Invoices")
                        .image("wallet")
                        .amount(allInvoices.getPaidInvoiceTotal())
                        .build());

        return AuthenticationResponse.builder()
                .firstname(user.getFirstName())
                .lastname(user.getLastName())
                .email(request.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getCompanyAddress())
                .postCode(user.getPostCode())
                .country(user.getCountry())
                .companyLogo(user.getCompanyLogo())
                .companyName(user.getCompanyName())
                .extraData(shortCutList)
                .recentInvoice(recentInvoice)
                .token(jwtToken)
                .status(HttpStatus.OK.value())
                .build();
    }

    public GeneralResponse verifyOtp(VerificationRequest request) {
        var user = userRepo.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, request.email())));

        String confirmationMsg = confirmOtpService.verifyOtpByUserId(request.otp(), user);
        user.setEnabled(Boolean.TRUE);
        userRepo.save(user);

        return GeneralResponse.builder()
                .status(HttpStatus.OK.value())
                .message(confirmationMsg)
                .build();
    }

    public GeneralResponse resendOtp(String email) {
        User user = userRepo.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));

        var otp = confirmOtpService.getOtp(user);
        String subject = "Resend OTP";
        String message = "Here is the OTP you requested for to complete the process " + otp;
        emailService.send(user.getFirstName(), email, message, subject);
        return GeneralResponse.builder()
                .status(HttpStatus.OK.value())
                .message("OTP has been resent check your email")
                .build();
    }

//    @CachePut(value = "otpCache", key = "#userId")
    public GeneralResponse forgotPassword(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));

        String otp = String.valueOf(new Random().nextInt(9000) + 1000);

        var confOtp = confirmOtpRepo.findByUser(user)
                .map(confirmOtp -> {
                    confirmOtp.setOtp(otp);
                    confirmOtp.setCreatedAt(LocalDateTime.now());
                    confirmOtp.setExpiresAt(LocalDateTime.now().plusMinutes(60 * 24));
                    confirmOtp.setUser(user);
                    return confirmOtp;
                })
                .orElseGet(() -> ConfirmOtp.builder()
                        .otp(otp)
                        .createdAt(LocalDateTime.now())
                        .expiresAt(LocalDateTime.now().plusMinutes(60 * 24))
                        .user(user)
                        .build()
                );
        confirmOtpRepo.save(confOtp);

        String subject = "Reset Password - OTP Verification";
        String message = "You are about to reset your password, use this OTP to complete the reset process " + otp;
        emailService.send(user.getFirstName(), user.getEmail(), message, subject);

        //       @TODO sendMail(email, message, subject );
        return GeneralResponse.builder()
                .status(HttpStatus.OK.value())
                .message("An otp has been sent to your email for verification")
                .build();
    }
//    public GeneralResponse forgotPassword(String email) {
//        System.out.println("=============");
//        User user = userRepo.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));
//        System.out.println("User found " + user.getLastName());
//
//        String otp = String.valueOf(new Random().nextInt(9000) + 1000);
//
//        var confOtp = confirmationOtpRepository.findByUser(user)
//                .map(confirmOtp -> {
//                    confirmOtp.setOtp(otp);
//                    confirmOtp.setCreatedAt(LocalDateTime.now());
//                    confirmOtp.setExpiresAt(LocalDateTime.now().plusMinutes(60 * 24));
//                    confirmOtp.setUser(user);
//                    return confirmOtp;
//                })
//                .orElseGet(() -> {
//                            ConfirmOtp confirmationOtp = ConfirmOtp.builder()
//                                    .otp(otp)
//                                    .createdAt(LocalDateTime.now())
//                                    .expiresAt(LocalDateTime.now().plusMinutes(60 * 24))
//                                    .user(user)
//                                    .build();
//                            return confirmationOtp;
//                        }
//                );
//        confirmationOtpRepository.save(confOtp);
//
//        System.out.println("OTP == " + otp);
//        String subject = "Reset Password - OTP Verification";
//        String message = "You are about to reset your password, use this OTP to complete the reset process " + otp;
//        emailService.send(user.getFirstName(), user.getEmail(), message, subject);
//
//        //       @TODO sendMail(email, message, subject );
//        return GeneralResponse.builder()
//                .message("An otp has been sent to your email for verification")
//                .build();
//    }

    public AuthenticationResponse resetPassword(AuthenticationRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, String.format(USER_NOT_FOUND, request.getEmail())));
//      @TODO ENSURE USER ARE NOT ABLE TO RESET PASSWORD TWICE WITHOUT CALLING FORGOT PASSWORD TWICE
        var confOtp = confirmOtpRepo.findByUser(user)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Invalid UserId"));
        if (LocalDateTime.now().isAfter(confOtp.getConfirmedAt().plusMinutes(5))) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Request Timed Out please try again");
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


//    ========================================

    @Cacheable(value = "otpCache", key = "#email")
    public GeneralResponse forgotPassword1(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND, email)));

        String otp = generateOtp();
        saveOtpToCache(email, otp);
        System.out.println("otp");
        System.out.println(otp);
        sendOtpByEmail(user, otp);

        return GeneralResponse.builder()
                .status(HttpStatus.OK.value())
                .message("An OTP has been sent to your email for verification")
                .build();
    }

    public GeneralResponse verifyOtp1(VerificationRequest request) {
        String cachedOtp = getOtpFromCache(request.email());
        System.out.println("cachedOtp");
        System.out.println(cachedOtp);
        if (cachedOtp != null && cachedOtp.equals(request.otp())) {
            clearOtpCache(request.email());
            // Perform additional actions, such as updating the password
            return GeneralResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("OTP verified successfully")
                    .build();
        } else {
            return GeneralResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Invalid OTP")
                    .build();
        }
    }

    private String generateOtp() {
        return String.valueOf(new Random().nextInt(9000) + 1000);
    }

    private void saveOtpToCache(String email, String otp) {
        otpCache.put(email, otp);
    }

    private String getOtpFromCache(String email) {
        return otpCache.get(email);
    }

    public void clearOtpCache(String email) {
        otpCache.remove(email);
    }


    private void sendOtpByEmail(User user, String otp) {
        String subject = "Reset Password - OTP Verification";
        String message = "You are about to reset your password, use this OTP to complete the reset process " + otp;
        emailService.send(user.getFirstName(), user.getEmail(), message, subject);
    }
}
