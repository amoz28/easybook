package uk.co.setech.easybook.dto;

import io.micrometer.core.instrument.util.StringEscapeUtils;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemsDto {
    private Integer id;
    private String service;
    private String description;
    private int quantity;
    private double price;
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        System.out.println("BLA NLA");
//        // Escape or sanitize the description to remove control characters
//        this.description = sanitizeDescription(description);
//    }
//
//    private String sanitizeDescription(String description) {
//        // Remove control characters or escape them as needed
//        // Here's an example using Apache Commons Lang library
//        return StringEscapeUtils.escapeJson(description); // Use escapeJson to escape control characters
//    }

}
