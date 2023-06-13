package uk.co.setech.easybook.exception;

import java.util.Date;

public record ErrorMessage(int status,
                           Date timestamp,
                           String message,
                           String description) {

}
