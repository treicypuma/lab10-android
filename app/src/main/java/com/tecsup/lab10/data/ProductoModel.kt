package com.tecsup.lab10.data

import com.google.gson.annotations.SerializedName

data class ProductoModel(
    @SerializedName("id")
    var id: Int,
    @SerializedName("nombre")
    var nombre: String,
    @SerializedName("precio")
    var precio: String,
    @SerializedName("categoria")
    var categoria: Int
)