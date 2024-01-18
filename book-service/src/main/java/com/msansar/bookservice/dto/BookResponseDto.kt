package com.msansar.bookservice.dto

import com.msansar.bookservice.model.Book
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import org.hibernate.annotations.GenericGenerator

data class BookResponseDto @JvmOverloads constructor(
        val id: BookIdResponseDto? = null,
        val title: String,
        val bookYear: Int,
        val author: String,
        val pressName: String,
        val isbn: String
){
    companion object{
        @JvmStatic
        fun convert(from: Book): BookResponseDto {
            return BookResponseDto(
                    from.id?.let { BookIdResponseDto.convert(it, from.isbn) },
                    from.title,
                    from.bookYear,
                    from.author,
                    from.pressName,
                    from.isbn
            )
        }
    }
}