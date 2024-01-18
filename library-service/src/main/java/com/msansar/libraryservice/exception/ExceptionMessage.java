package com.msansar.libraryservice.exception;

// Oluşturacağımız exception sınıflarına göre bir ExceptionMessage nesnesi olşuturduk
// Hata durumlarında sadece bir string yollayabiliriz. Herhangi bir yapıda olmak zorunda değil.
// Biz custom bir yapı yapmak istediğimiz için bu sınıfı yaptık. ResponseEntity her şeyi JSON a çevirebiliyor.
// Yani biz bu nesneyi json olarak göndereceğiz
public record ExceptionMessage(String timestamp, int status, String error, String message, String path) {

}
