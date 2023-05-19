package uk.co.setech.EasyBook.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class InvoiceSummary {
    private String title;
    private String image;
    private double amount;
}
