package uk.co.setech.EasyBook.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationResponse {
    private String firstname;
    private String lastname;
    private String email;
    private Double overdueInvoice;
    private String token;
}
