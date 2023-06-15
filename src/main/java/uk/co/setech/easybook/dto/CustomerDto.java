package uk.co.setech.easybook.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerDto {
    private Long id;
    private String displayname;
    private String firstname;
    private String lastname;
    private String email;
    private String phonenumber;
    private String address;
    private String companyname;
    private String taxNumber;
    private String postcode;
    private String county;
    private String country;
}
