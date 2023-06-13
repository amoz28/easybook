package uk.co.setech.easybook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VATDto {
    private Long id;
    private String percentage;
    private String label;
    private String tag;
}
