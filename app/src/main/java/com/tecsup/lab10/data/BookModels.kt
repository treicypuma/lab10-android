package com.tecsup.lab10.data

import kotlinx.serialization.Serializable

@Serializable
data class VolumeResponse(val items: List<Book> = emptyList())

@Serializable
data class Book(val id: String, val volumeInfo: VolumeInfo)

@Serializable
data class VolumeInfo(
    val title: String = "",
    val authors: List<String> = emptyList(),
    val imageLinks: ImageLinks? = null
)

@Serializable
data class ImageLinks(val thumbnail: String? = null)