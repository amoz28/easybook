package uk.co.setech.easybook.dto;


import jakarta.validation.constraints.NotNull;

public record VerificationRequest(
        @NotNull String email,
        @NotNull String otp) {
}
