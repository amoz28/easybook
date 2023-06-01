package uk.co.setech.easybook.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class AuthenticationResponse {
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String companyName;
    private String companyLogo;
    private String address;
    private String postCode;
    private String country;
    private ArrayList<InvoiceSummary> extraData;
    private List<InvoiceDto>recentInvoice;
    private String token;
}
