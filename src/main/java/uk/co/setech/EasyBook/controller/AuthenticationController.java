package uk.co.setech.EasyBook.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.setech.EasyBook.dto.AuthenticationRequest;
import uk.co.setech.EasyBook.dto.AuthenticationResponse;
import uk.co.setech.EasyBook.dto.GeneralResponse;
import uk.co.setech.EasyBook.dto.RegisterRequest;
import uk.co.setech.EasyBook.dto.VerificationRequest;
import uk.co.setech.EasyBook.service.impl.AuthenticationService;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<GeneralResponse> register(@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(service.authentication(request));
    }

    @PostMapping("/verifyaccount")
    public ResponseEntity<GeneralResponse> verifyOtp(@RequestBody VerificationRequest request){
        return ResponseEntity.ok(service.verifyOtp(request));
    }

    @GetMapping("/resendotp")
    public ResponseEntity<GeneralResponse> resendotp(@RequestParam String email){
        return ResponseEntity.ok(service.resendOtp(email));
    }

    @GetMapping("/forgotpassword")
    public ResponseEntity<GeneralResponse> forgotpassword(@RequestParam String email){
        return ResponseEntity.ok(service.forgotPassword(email));
    }

    @PostMapping("/resetpassword")
    public ResponseEntity<AuthenticationResponse> resetpassword(@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(service.resetPassword(request));
    }

}
