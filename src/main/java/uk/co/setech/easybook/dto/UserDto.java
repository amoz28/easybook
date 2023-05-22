package uk.co.setech.easybook.dto;

import jakarta.persistence.Lob;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uk.co.setech.easybook.enums.Role;

@Getter
@Setter
@Builder
public class UserDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String companyName;
    @Lob
    private byte[] companyLogo;
    private String website;
    private String companyAddress;
    private String city;
    private String county;
    private String country;
    private String postCode;
    private Role role;
    private Boolean locked;

    private Boolean enabled;

}
