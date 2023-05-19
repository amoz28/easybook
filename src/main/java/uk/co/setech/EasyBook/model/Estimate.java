package uk.co.setech.EasyBook.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@SuperBuilder
@RequiredArgsConstructor
public class Estimate {

    @Id
    @GeneratedValue
    private Long id;
    private LocalDate duedate;
    private LocalDate servicedate;
    private Double rate;
    private String vat;
    private double subtotal;
    private double tax;
    private double total;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "estimate_id")
    @JsonManagedReference
    private List<EstimateItem> items;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}



