package uk.co.setech.easybook.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter
public class InvoiceTemplate extends BaseEntity {
    @Column(unique = true)
    private String name;
    private String content;
}
