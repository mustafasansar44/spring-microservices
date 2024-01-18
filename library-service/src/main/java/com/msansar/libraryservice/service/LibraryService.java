package com.msansar.libraryservice.service;

import com.msansar.libraryservice.client.BookServiceClient;
import com.msansar.libraryservice.dto.AddBookRequestDto;
import com.msansar.libraryservice.dto.LibraryDto;
import com.msansar.libraryservice.exception.LibraryNotFoundException;
import com.msansar.libraryservice.model.Library;
import com.msansar.libraryservice.repository.LibraryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibraryService {
    private final LibraryRepository repository;
    private final BookServiceClient bookServiceClient;

    public LibraryService(LibraryRepository repository, BookServiceClient bookServiceClient) {
        this.repository = repository;
        this.bookServiceClient = bookServiceClient;
    }

    public LibraryDto getAllBooksInLibraryById(String id){
        Library library = repository.findById(id)
                .orElseThrow(() -> new LibraryNotFoundException("Library Bulunamadı!"));
        LibraryDto libraryDto = new LibraryDto(
                library.getId(),
                library.getUserBook().stream()
                        .map(bookServiceClient::getBookById)
                        .map(ResponseEntity::getBody)
                        .collect(Collectors.toList())
        );
        return libraryDto;
    }

    public LibraryDto createLibrary(){
        Library newLibrary = new Library();
        repository.save(newLibrary);
        return new LibraryDto(newLibrary.getId());
    }

    public void addBookToLibrary(AddBookRequestDto requestDto){
        String bookId = bookServiceClient.getBookByIsbn(requestDto.getIsbn()).getBody().getId();
        Library library = repository.findById(requestDto.getId())
                .orElseThrow(() -> new LibraryNotFoundException("Library Bulunamadı!"));

        library.getUserBook()
                .add(bookId);

        repository.save(library);
    }

    public List<String> getAllLibraries(){
        return repository.findAll().stream()
                .map(Library::getId)
                .collect(Collectors.toList());
    }
}
