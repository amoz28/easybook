package uk.co.setech.EasyBook.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.setech.EasyBook.dto.*;
import uk.co.setech.EasyBook.service.impl.AuthenticationService;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<GeneralResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authentication(request));
    }

    @PostMapping("/verifyAccount")
    public ResponseEntity<GeneralResponse> verifyOtp(@RequestBody VerificationRequest request) {
        return ResponseEntity.ok(service.verifyOtp(request));
    }

    @GetMapping("/resendOtp")
    public ResponseEntity<GeneralResponse> resendotp(@RequestParam String email) {
        return ResponseEntity.ok(service.resendOtp(email));
    }

    @GetMapping("/forgotPassword")
    public ResponseEntity<GeneralResponse> forgotpassword(@RequestParam String email) {
        return ResponseEntity.ok(service.forgotPassword(email));
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<AuthenticationResponse> resetpassword(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.resetPassword(request));
    }

}
