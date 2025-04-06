package uk.co.setech.easybook.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceTemplateDto {
    private String id;
    private String name, content;
}
