package com.msansar.bookservice;

import com.msansar.bookservice.model.Book;
import com.msansar.bookservice.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.Arrays;

@SpringBootApplication
public class BookServiceApplication implements CommandLineRunner {

	private final BookRepository bookRepository;

	public BookServiceApplication(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(BookServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Book book1 = new Book("Kitap1", 2013, "Yazar1", "PressName1", "Isbn1");
		Book book2 = new Book("Kitap2", 2014, "Yazar2", "PressName2", "Isbn2");
		Book book3 = new Book("Kitap3", 2015, "Yazar3", "PressName3", "Isbn3");

		bookRepository.saveAll(Arrays.asList(book1, book2, book3));
	}
}
