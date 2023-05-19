package uk.co.setech.EasyBook.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.co.setech.EasyBook.model.Customer;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@SuperBuilder
public class InvoiceDto {
    private Long id;
    private LocalDate duedate;
    private LocalDate servicedate;
    private LocalDate lastReminderDate;
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
