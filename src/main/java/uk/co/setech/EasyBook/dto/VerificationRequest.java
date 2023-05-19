package uk.co.setech.EasyBook.dto;


import jakarta.validation.constraints.NotNull;

public record VerificationRequest(
        @NotNull String email,
        @NotNull String otp) {
}
