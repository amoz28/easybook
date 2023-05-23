package uk.co.setech.easybook.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.co.setech.easybook.model.Customer;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@RequiredArgsConstructor
public class EstimateDto {
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
