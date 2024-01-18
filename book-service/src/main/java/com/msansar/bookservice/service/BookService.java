package com.msansar.bookservice.service;

import com.msansar.bookservice.dto.BookIdResponseDto;
import com.msansar.bookservice.dto.BookResponseDto;
import com.msansar.bookservice.exception.BookNotFoundException;
import com.msansar.bookservice.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<BookResponseDto> getAll(){
        return repository.findAll().stream()
                .map(BookResponseDto::convert)
                .collect(Collectors.toList());
    }

    public BookIdResponseDto findByIsbn(String isbn){

        return repository.findBookByIsbn(isbn).map(
                book -> new BookIdResponseDto(book.getId(), book.getIsbn())
        ).orElseThrow(() -> new BookNotFoundException("Isbn Bulunamadı!"));
    }

    public BookResponseDto findBookDetailsById(String id){
        return repository.findById(id).map(BookResponseDto::convert)
                .orElseThrow(() -> new BookNotFoundException("id Bulunamadı!"));
    }
}
