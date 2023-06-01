package uk.co.setech.easybook.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.co.setech.easybook.enums.InvoiceType;

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
    private LocalDate lastReminderDate;
    private Double rate;
    private String vat;
    private double subtotal;
    private double tax;
    private double total;
    private List<ItemsDto> items;
//    private List<InvoiceItem> items;
    private String customerEmail;
    private String invoiceInfo;
    private boolean isInvoicePaid;
    private Integer customerId;
    private InvoiceType type;
}
