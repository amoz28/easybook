package uk.co.setech.easybook.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Customer extends BaseEntity {
    private String displayname;
    private String firstname;
    private String lastname;
    private String email;
    private String phonenumber;
    private String address;
    private String companyname;
    private String taxNumber;
    private String postcode;
    private String country;
    @Column(nullable = false)
    private long userId;
}
