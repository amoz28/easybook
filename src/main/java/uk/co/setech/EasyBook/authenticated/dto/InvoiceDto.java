package uk.co.setech.EasyBook.authenticated.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.SuperBuilder;
import uk.co.setech.EasyBook.authenticated.model.Customer;
import uk.co.setech.EasyBook.authenticated.model.InvoiceItem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@RequiredArgsConstructor
public class InvoiceDto {
    private Long id;
    private LocalDate duedate;
    private LocalDate servicedate;
    private Double rate;
    private String vat;
    private double subtotal;
    private double tax;
    private double total;
    private List<ItemsDto> items;
    private String customerEmail;
    private boolean isInvoicePaid;
    @JsonIgnore
    private Customer customer;
}
