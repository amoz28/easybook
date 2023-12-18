package uk.co.setech.easybook.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import uk.co.setech.easybook.enums.InvoiceType;
import uk.co.setech.easybook.enums.PaymentType;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
public class Invoice extends BaseEntity {
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

    @Column(nullable = false)
    private boolean isInvoiceSent;

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

//    private double outstandingBalance;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Column(nullable = false)
    private double amountPaid = 0.00;
}
