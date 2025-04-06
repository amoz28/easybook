package uk.co.setech.easybook.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.setech.easybook.dto.AuthenticationRequest;
import uk.co.setech.easybook.dto.AuthenticationResponse;
import uk.co.setech.easybook.dto.GeneralResponse;
import uk.co.setech.easybook.dto.RegisterRequest;
import uk.co.setech.easybook.dto.VerificationRequest;
import uk.co.setech.easybook.service.AuthenticationService;


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
