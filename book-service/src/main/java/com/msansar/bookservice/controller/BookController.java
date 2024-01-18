package com.msansar.bookservice.controller;

import com.msansar.bookservice.dto.BookIdResponseDto;
import com.msansar.bookservice.dto.BookResponseDto;
import com.msansar.bookservice.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {

    Logger logger = LoggerFactory.getLogger(BookController.class);
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<BookResponseDto>> getAllBook(){
        return ResponseEntity.ok(bookService.getAll());
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookIdResponseDto> getBookByIsbn(@PathVariable @NotEmpty String isbn){
        logger.info("BOOK REQUESTED BY ISBN: " + isbn);
        return ResponseEntity.ok(bookService.findByIsbn(isbn));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable @NotEmpty String id){
        return ResponseEntity.ok(bookService.findBookDetailsById(id));
    }

}
