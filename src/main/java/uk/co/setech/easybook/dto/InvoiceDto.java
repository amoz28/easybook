package uk.co.setech.easybook.dto;

import lombok.Getter;
import lombok.Setter;
import uk.co.setech.easybook.enums.InvoiceType;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class InvoiceDto {
    private Long id;
    private LocalDate duedate;
    private LocalDate servicedate;
    private LocalDate lastReminderDate;
    private Double rate;
    private Double vat;
    private double subtotal;
    private double tax;
    private double total;
    private List<ItemsDto> items;
    private String invoiceInfo;
    private boolean isInvoicePaid;
    private Integer customerId;
    private InvoiceType type;
    private Long outstandingBalance;
}
