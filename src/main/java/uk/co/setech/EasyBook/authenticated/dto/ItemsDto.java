package uk.co.setech.EasyBook.authenticated.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
public class ItemsDto {
    private Integer id;
    private String service;
    private String description;
    private Integer quantity;

}
