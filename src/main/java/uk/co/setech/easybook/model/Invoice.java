package uk.co.setech.easybook.model;

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
public class Invoice {

    @Id
    @GeneratedValue
    private Long id;
    private LocalDate duedate;
    private LocalDate servicedate;
    private LocalDate lastReminderDate;
    private Double rate;
    private String vat;
    private double subtotal;
    private double tax;
    private double total;
    private boolean isInvoicePaid;
    private String type;
    private String recordDescription;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "invoice_id")
    @JsonManagedReference
    private List<InvoiceItem> items;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}



