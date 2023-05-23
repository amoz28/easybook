package uk.co.setech.easybook.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateCustomerDto {
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
