package com.msansar.bookservice.dto

data class BookIdResponseDto @JvmOverloads constructor(
        val id: String? = "",
        val isbn: String
){
    companion object{
        @JvmStatic
        fun convert(id: String, isbn: String): BookIdResponseDto {
            return BookIdResponseDto(id, isbn)
        }
    }
}