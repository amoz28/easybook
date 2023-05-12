package uk.co.setech.EasyBook.exception;

import java.io.IOException;
import java.util.Date;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice("uk.co.setech.EasyBook.config")
public class ControllerExceptionHandler {

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<ErrorMessage> userNotFoundException(UsernameNotFoundException ex, WebRequest request) {
    ErrorMessage message = new ErrorMessage(
            HttpStatus.NOT_FOUND.value(),
            new Date(),
            ex.getMessage(),
            request.getDescription(false));

    return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<ErrorMessage> expiredJwtException(ExpiredJwtException ex, WebRequest request) {
    ErrorMessage message = new ErrorMessage(
            HttpStatus.UNAUTHORIZED.value(),
            new Date(),
            ex.getMessage(),
            request.getDescription(false));

    return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
  }
  @ExceptionHandler(HttpClientErrorException.Forbidden.class)
  public ResponseEntity<ErrorMessage> httpClientErrorException(HttpClientErrorException.Forbidden ex, WebRequest request) {
    ErrorMessage message = new ErrorMessage(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        new Date(),
        ex.getMessage(),
        request.getDescription(false));

    return new ResponseEntity<ErrorMessage>(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }


  @ExceptionHandler(ServletException.class)
  public ResponseEntity<ErrorMessage> globalServletException(ServletException ex, WebRequest request) {
    ErrorMessage message = new ErrorMessage(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            new Date(),
            ex.getMessage(),
            request.getDescription(false));

    return new ResponseEntity<ErrorMessage>(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(IOException.class)
  public ResponseEntity<ErrorMessage> globalIOException(IOException ex, WebRequest request) {
    ErrorMessage message = new ErrorMessage(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            new Date(),
            ex.getMessage(),
            request.getDescription(false));

    return new ResponseEntity<ErrorMessage>(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }
//  @ExceptionHandler(Exception.class)
//  public ResponseEntity<ErrorMessage> globalExceptionHandler(Exception ex, WebRequest request) {
//    ErrorMessage message = new ErrorMessage(
//        HttpStatus.INTERNAL_SERVER_ERROR.value(),
//        new Date(),
//        ex.getMessage(),
//        request.getDescription(false));
//
//    return new ResponseEntity<ErrorMessage>(message, HttpStatus.INTERNAL_SERVER_ERROR);
//  }

}
