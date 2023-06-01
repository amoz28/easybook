package uk.co.setech.easybook.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemsDto {
    private Integer id;
    private String service;
    private String description;
    private int quantity;
    private double price;

}
