package com.msansar.libraryservice;

import com.msansar.libraryservice.client.RetrieveMessageErrorDecoder;
import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class LibraryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryServiceApplication.class, args);
	}

	// Yazdığım RetrieveMessageErrorDecoder için bir bean oluşturması için kullanıyoruz.
	// mesele 500 dönmemesi için o kadar uğraşıyoruz aslında. Dönen mesajın anlamlı olması gerekir.


	/*
	FaultTolerance kullanacağım için bunlar commendline
	FEIGN CLIENT error handling
	@Bean
	public ErrorDecoder errorDecoder(){
		return new RetrieveMessageErrorDecoder();
	}


	// Full hata mesajlarının olduğu bir hale getirdik.
	@Bean
	Logger.Level feignLoggerLevel(){
		return Logger.Level.FULL;
	}
	*/
}
