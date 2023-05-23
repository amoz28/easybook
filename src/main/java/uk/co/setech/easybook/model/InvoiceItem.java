package uk.co.setech.easybook.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String service;
    private String description;
    private double price;
    private int quantity;

    @ManyToOne
    @JsonBackReference
    private Invoice invoice;
}
