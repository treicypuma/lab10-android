package com.tecsup.lab10.data

import retrofit2.http.GET
import retrofit2.http.Query

interface BookshelfApiService {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int
    ): VolumeResponse
}

interface BooksRepository {
    suspend fun getBooks(query: String): List<Book>
}

class NetworkBooksRepository(
    private val api: BookshelfApiService
) : BooksRepository {
    override suspend fun getBooks(query: String): List<Book> =
        api.searchBooks(query, 30).items
}