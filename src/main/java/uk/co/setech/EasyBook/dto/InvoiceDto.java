package uk.co.setech.EasyBook.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import uk.co.setech.EasyBook.model.Customer;

import java.time.LocalDate;
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
    @JsonIgnore
    private Customer customer;
}
