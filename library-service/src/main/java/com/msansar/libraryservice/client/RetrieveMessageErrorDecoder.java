package com.msansar.libraryservice.client;

import com.msansar.libraryservice.exception.BookNotFoundException;
import com.msansar.libraryservice.exception.ExceptionMessage;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

// eureka, başka servisten aldığımız cevabın hata mı değil mi anlamak için dönen değerin status koduna bakar.
// eğer 2xx değilse hatadır. Biz eğer ErrorDecoder'i implement etmezsek herhangi bir error'u decode edemeyiz.
public class RetrieveMessageErrorDecoder implements ErrorDecoder {

    // Decoder'i kendin de yazabilirsin ya da buradaki gibi default olanı kullanabilirsin.
    // Aldığı feignException'u ----> exception yapar
    // Biz bu default'u kendimize göre customize edeceğiz.
    // Bu decode implementasyonu her feign hatası aldığında çalışır!
    // Bu nesneyi sadece eğer bizim bir error'u handle edemezse switch'in default'unda devam ettirmek için oluşturduk.
    private final ErrorDecoder errorDecoder = new Default();

    // s = methodKey
    // Burası aslında, biz karşı serviste bir exception alırsak mesela BookNotFoundException alırsak aşağıdaki metod ile bunu convert ederek kendi servisimizin anlayacağı yapıya çevireceğiz.
    @Override
    public Exception decode(String methodKey, Response response) {
        ExceptionMessage message = null;
        try(InputStream body = response.body().asInputStream()){
            message = new ExceptionMessage(
                    (String) response.headers().get("date").toArray()[0], // response.headers() bir map döner. get("date") ise sadece 1 değer döner ama dizi şeklinde döndüğü için [0]'ını alıyoruz.
                    response.status(), // Diğer serviste alacağımız hatanın statusCode'u
                    HttpStatus.resolve(response.status()).getReasonPhrase(), // getReasonPhrase aldığımız status'un string halini döner.
                    IOUtils.toString(body, StandardCharsets.UTF_8), // Diğer serviste alacağımız hatanın mesajını burada tekrar ayarlıyoruz.
                    response.request().url()
            );
        }catch (IOException e){
            return new Exception(e.getMessage());
        }

        // Şimdi ben bir hata fırlatmam gerekiyor ki libraryService bu hatayı yakalayabilsin.
        // Book_Service'de return new CustomError şeklinde olan, tanımladığın bütün hataları yazmalısın.
        switch (response.status()){
            case 404:
                throw new BookNotFoundException(message);
            default:
                // Benim handle etmek istediğm case'lerin dışındaki durumları default'a pasladık kısaca.
                return errorDecoder.decode(methodKey, response);
        }
    }
}
