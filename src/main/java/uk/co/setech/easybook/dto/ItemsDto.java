package uk.co.setech.easybook.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ItemsDto {
    private Long id;
    private String service;
    private String description;
    private int quantity;
    private double price;
}
