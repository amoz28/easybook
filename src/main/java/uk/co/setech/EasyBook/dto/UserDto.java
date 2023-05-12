package uk.co.setech.EasyBook.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uk.co.setech.EasyBook.enums.Role;

@Getter
@Setter
@Builder
public class UserDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String companyName;
    private String companyLogo;
    private String website;
    private String companyAddress;
    private String city;
    private String county;
    private String postCode;
    private Role role;
    private Boolean locked;
    private Boolean enabled;

}
