package uk.co.setech.EasyBook.authenticated.model;

import jakarta.persistence.*;
import lombok.*;
import uk.co.setech.EasyBook.user.User;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue
    private Integer id;
    private String displayname;
    private String firstname;
    private String lastname;
    private String email;
    private String phonenumber;
    private String address;
    private String companyname;
    private String postcode;
    private String country;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
