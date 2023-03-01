package uk.co.setech.EasyBook.authenticated.exception;

import java.util.Date;

public record ErrorMessage ( int statusCode,
   Date timestamp,
   String message,
   String description){

}
