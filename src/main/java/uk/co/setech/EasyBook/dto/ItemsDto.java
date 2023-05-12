package uk.co.setech.EasyBook.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemsDto {
    private Integer id;
    private String service;
    private String description;
    private Integer quantity;
    private double price;

}
