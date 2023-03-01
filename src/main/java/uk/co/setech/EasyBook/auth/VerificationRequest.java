package uk.co.setech.EasyBook.auth;


import javax.validation.constraints.NotNull;

public record VerificationRequest(
        @NotNull String email,
        @NotNull String otp) {}
