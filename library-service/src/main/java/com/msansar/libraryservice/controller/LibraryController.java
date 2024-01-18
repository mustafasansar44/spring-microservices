package com.msansar.libraryservice.controller;

import com.msansar.libraryservice.dto.AddBookRequestDto;
import com.msansar.libraryservice.dto.LibraryDto;
import com.msansar.libraryservice.service.LibraryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/library")
public class LibraryController {
    private final LibraryService libraryService;
    private final Environment environment;
    private final Logger logger = LoggerFactory.getLogger(LibraryController.class);

    @Value("${library.service.count}")
    private Integer count;

    public LibraryController(LibraryService libraryService, Environment environment) {
        this.libraryService = libraryService;
        this.environment = environment;
    }

    @GetMapping("{id}")
    public ResponseEntity<LibraryDto> getLibraryById(@PathVariable String id){
        return ResponseEntity.ok(libraryService.getAllBooksInLibraryById(id));
    }

    @PostMapping
    public ResponseEntity<LibraryDto> createLibrary(){
        logger.info("Library Oluşturuldu. PORT Numarası: " + environment.getProperty("local.server.port"));
        return ResponseEntity.ok(libraryService.createLibrary());
    }

    @PutMapping
    public ResponseEntity<Void> addBookToLibrary(@RequestBody AddBookRequestDto requestDto){
        libraryService.addBookToLibrary(requestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<String>> getAllLibraries(){
        return ResponseEntity.ok(libraryService.getAllLibraries());
    }

    @GetMapping("/getyml")
    public String getYml(){
        return "COUNT: " + count.toString();
    }

}
