package com.msansar.libraryservice.client;

import com.msansar.libraryservice.dto.BookIdResponseDto;
import com.msansar.libraryservice.dto.BookResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Buradaki amaç aslında hata durumuna dayanıklı hale gelmek.Yanlış isbn veya id ile sorgulasak bile hata vermedik default değerler gönderdik.
 * Amaç kullanıcıya bir şekilde yanlışta olsa sonuç döndermek.
 */
@FeignClient(name = "book-service", path = "/book")
public interface BookServiceClient {

    Logger logger = LoggerFactory.getLogger(BookServiceClient.class);

    @GetMapping("/isbn/{isbn}")
    // applicationContext'e getBookByIsbnCircuitBreaker olarak kaydedilcek
    @CircuitBreaker(name = "getBookByIsbnCircuitBreaker", fallbackMethod = "getBookByIsbnFallback")
    ResponseEntity<BookIdResponseDto> getBookByIsbn(@PathVariable String isbn);

    // Bu method her zaman gönderdiğimiz parametre varsa alır ve exception alır.
    // Bir component içerisnde de tanımlayabilirdik. Biz interface içerisinde tanımladık.
    // Şuanda bu metodu hiçbir şey kullanmıyor ama fallbackMethod = ile tanımladığımız alan sayesinde
    // burası ayrı bir thread oluşturulup çalıştırılacak
    // getBookByIsbn çalışştığında alacağım herhangi bir feign hatasında çalışacak kısaca.
    default ResponseEntity<BookIdResponseDto> getBookByIsbnFallback(
            String isbn, // Fallback yaptığım metodun parametresi
            Exception e // CircuitBreaker tarafından üretilir. bu exception feign'den geldi. FeignException'dan exception'a çevrilmiş oldu
    ){
        logger.info("Book Not Found By Isbn" + isbn + " returning default BookDto object");
        return ResponseEntity.ok(
            new BookIdResponseDto(
                    "default_id",
                    "default_isbn"
            )
        );
    }

    @GetMapping("/{id}")
    @CircuitBreaker(name = "getBookByIdCircuitBreaker", fallbackMethod = "getBookByIdFallback")
    ResponseEntity<BookResponseDto> getBookById(@PathVariable String id);

    default ResponseEntity<BookResponseDto> getBookByIdFallback(String id, Exception e){
        logger.info("Book Not Found By Isbn" + id + " returning default BookDto object");
        return ResponseEntity.ok(
                new BookResponseDto(
                    new BookIdResponseDto("default_id", "default_isbn"),
                        "default_title",
                        2024,
                        "default_author",
                        "default_pressName"
                )
        );
    }

    @GetMapping
    ResponseEntity<List<BookResponseDto>> getAllBook();

}
