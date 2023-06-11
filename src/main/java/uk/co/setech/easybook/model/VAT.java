package uk.co.setech.easybook.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class VAT extends BaseEntity {
    private String percentage;
    private String label;
    private String tag;
}
