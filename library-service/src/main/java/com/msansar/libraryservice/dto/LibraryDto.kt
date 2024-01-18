package com.msansar.libraryservice.dto

import java.util.ArrayList

data class LibraryDto @JvmOverloads constructor(
        val id: String,
        val userBookMap: List<BookResponseDto>? = ArrayList()
)