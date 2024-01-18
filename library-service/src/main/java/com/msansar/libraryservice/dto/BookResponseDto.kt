package com.msansar.libraryservice.dto


data class BookResponseDto @JvmOverloads constructor(
        val id: BookIdResponseDto? = null,
        val title: String? = "",
        val bookYear: Int? = 0,
        val author: String? = "",
        val pressName: String? = "",
)