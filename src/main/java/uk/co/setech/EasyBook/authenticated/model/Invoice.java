package uk.co.setech.EasyBook.authenticated.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


import java.time.LocalDate;
import java.util.List;
@Data
@Entity
@SuperBuilder
@RequiredArgsConstructor
public class Invoice {

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
    @JoinColumn(name = "invoice_id")
    @JsonManagedReference
    private List<InvoiceItem> items;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

}



