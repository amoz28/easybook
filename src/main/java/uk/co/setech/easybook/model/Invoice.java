package uk.co.setech.easybook.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.co.setech.easybook.enums.InvoiceType;

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
    private Double vat;
    @Column(nullable = false)
    private double subtotal;
    @Column(nullable = false)
    private double total;
    @Column(nullable = false)
    private boolean isInvoicePaid;
    private String invoiceInfo;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "invoice_id")
    @JsonManagedReference
    private List<InvoiceItem> items;

    @Column(nullable = false)
    private Integer customerId;

    @Column(nullable = false)
    private long userId;

    @Enumerated(EnumType.STRING)
    private InvoiceType type;
}



