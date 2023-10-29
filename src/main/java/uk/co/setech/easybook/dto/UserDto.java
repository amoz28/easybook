package uk.co.setech.easybook.dto;

import lombok.*;
import uk.co.setech.easybook.enums.Role;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String companyName;
    private String companyLogo;
    private String website;
    private String companyAddress;
    private String city;
    private String county;
    private String country;
    private String postCode;
    private String companyRegistrationNo;
    private String accountNo;
    private String sortCode;
    private String accountName;
    private Role role;
    private Boolean locked;
    private Boolean enabled;

}
