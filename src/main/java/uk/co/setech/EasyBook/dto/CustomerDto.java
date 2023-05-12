package uk.co.setech.EasyBook.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerDto {
    private String displayname;
    private String firstname;
    private String lastname;
    private String email;
    private String phonenumber;
    private String address;
    private String companyname;
    private String postcode;
    private String country;
}
