package com.tecsup.lab10.view

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.tecsup.lab10.data.ProductoApiService
import com.tecsup.lab10.data.ProductoModel
import kotlinx.coroutines.launch

@Composable
fun ContenidoProductosListado(navController: NavHostController, servicio: ProductoApiService) {
    val lista: SnapshotStateList<ProductoModel> = remember { mutableStateListOf() }

    LaunchedEffect(Unit) {
        try {
            val listado = servicio.selectProductos()
            lista.clear()
            listado.forEach { lista.add(it) }
        } catch (e: Exception) {
            Log.e("PRODUCTO-LISTA", "Error: ${e.message}")
        }
    }

    LazyColumn {
        item {
            Row(
                modifier = Modifier.fillParentMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("ID", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.1f))
                Text("PRODUCTO", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f))
                Text("PRECIO", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.2f))
                Text("Accion", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.2f))
            }
        }

        items(lista) { item ->
            Row(
                modifier = Modifier.padding(start = 8.dp).fillParentMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${item.id}", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.1f))
                Text(item.nombre, fontSize = 16.sp, modifier = Modifier.weight(0.5f))
                Text(item.precio, fontSize = 16.sp, modifier = Modifier.weight(0.2f))
                IconButton(
                    onClick = { navController.navigate("productoVer/${item.id}") },
                    Modifier.weight(0.1f)
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Editar")
                }
                IconButton(
                    onClick = { navController.navigate("productoDel/${item.id}") },
                    Modifier.weight(0.1f)
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}

@Composable
fun ContenidoProductoEditar(
    navController: NavHostController,
    servicio: ProductoApiService,
    pid: Int = 0
) {
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("1") }
    val scope = rememberCoroutineScope()

    if (pid != 0) {
        LaunchedEffect(Unit) {
            try {
                val obj = servicio.selectProducto(pid.toString())
                nombre = obj.body()?.nombre ?: ""
                precio = obj.body()?.precio ?: ""
                categoria = obj.body()?.categoria?.toString() ?: "1"
            } catch (e: Exception) {
                Log.e("PRODUCTO-EDITAR", "Error: ${e.message}")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = pid.toString(),
            onValueChange = { },
            label = { Text("ID (solo lectura)") },
            readOnly = true,
            singleLine = true
        )
        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre:") },
            singleLine = true
        )
        TextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio (ej. 2.50):") },
            singleLine = true
        )
        TextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoria (id, ej. 1):") },
            singleLine = true
        )
        Button(
            onClick = {
                scope.launch {
                    val obj = ProductoModel(
                        pid, nombre, precio, categoria.toIntOrNull() ?: 1
                    )
                    try {
                        if (pid == 0)
                            servicio.insertProducto(obj)
                        else
                            servicio.updateProducto(pid.toString(), obj)
                    } catch (e: Exception) {
                        Log.e("PRODUCTO-GRABAR", "Error: ${e.message}")
                    }
                    navController.navigate("productos")
                }
            }
        ) {
            Text("Grabar", fontSize = 16.sp)
        }
    }
}

@Composable
fun ContenidoProductoEliminar(navController: NavHostController, servicio: ProductoApiService, id: Int) {
    var showDialog by remember { mutableStateOf(true) }
    var borrar by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                navController.navigate("productos")
            },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Está seguro de eliminar el Producto?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        borrar = true
                    }
                ) { Text("Aceptar") }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        navController.navigate("productos")
                    }
                ) { Text("Cancelar") }
            }
        )
    }

    if (borrar) {
        LaunchedEffect(Unit) {
            try {
                servicio.deleteProducto(id.toString())
            } catch (e: Exception) {
                Log.e("PRODUCTO-ELIMINAR", "Error: ${e.message}")
            }
            borrar = false
            navController.navigate("productos")
        }
    }
}