package uk.co.setech.easybook.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
public class GeneralResponse {
    private String message;
    private int status;
}
