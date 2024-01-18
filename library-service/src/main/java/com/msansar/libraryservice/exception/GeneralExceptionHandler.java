package com.msansar.libraryservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
// Exception sınıflarını yazarken metod isimleri aynı olsun. Method overloading kullan.
// çünkü eğer 2 tane BookNotFoundException varsa bile adlar farklı olursa ide hata vermeyecektir.
@RestControllerAdvice
public class GeneralExceptionHandler {
    @ExceptionHandler(LibraryNotFoundException.class)
    public ResponseEntity<?> handleLibraryNotFoundException(LibraryNotFoundException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ExceptionMessage> handleBookNotFoundException(BookNotFoundException exception){
        return new ResponseEntity<>(
                exception.getExceptionMessage(),
                HttpStatus.resolve(exception.getExceptionMessage().status())
        );
    }
}