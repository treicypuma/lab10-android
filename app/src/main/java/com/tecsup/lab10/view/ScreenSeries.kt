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
import com.tecsup.lab10.data.SerieApiService
import com.tecsup.lab10.data.SerieModel
import kotlinx.coroutines.launch

@Composable
fun ContenidoSeriesListado(navController: NavHostController, servicio: SerieApiService) {
    val listaSeries: SnapshotStateList<SerieModel> = remember { mutableStateListOf() }

    LaunchedEffect(Unit) {
        try {
            val listado = servicio.selectSeries()
            listaSeries.clear()
            listado.forEach { listaSeries.add(it) }
        } catch (e: Exception) {
            Log.e("SERIE-LISTA", "Error: ${e.message}")
        }
    }

    LazyColumn {
        item {
            Row(
                modifier = Modifier.fillParentMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.1f)
                )
                Text(
                    text = "SERIE",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.7f)
                )
                Text(
                    text = "Accion",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.2f)
                )
            }
        }

        items(listaSeries) { item ->
            Row(
                modifier = Modifier.padding(start = 8.dp).fillParentMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.id}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.1f)
                )
                Text(
                    text = item.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.6f)
                )
                IconButton(
                    onClick = {
                        navController.navigate("serieVer/${item.id}")
                        Log.e("SERIE-VER", "ID = ${item.id}")
                    },
                    Modifier.weight(0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Editar",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                IconButton(
                    onClick = {
                        navController.navigate("serieDel/${item.id}")
                        Log.e("SERIE-DEL", "ID = ${item.id}")
                    },
                    Modifier.weight(0.1f)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Eliminar",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

@Composable
fun ContenidoSerieEditar(
    navController: NavHostController,
    servicio: SerieApiService,
    pid: Int = 0
) {
    val id = pid
    var name by remember { mutableStateOf("") }
    var release_date by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    if (id != 0) {
        LaunchedEffect(Unit) {
            try {
                val objSerie = servicio.selectSerie(id.toString())
                name = objSerie.body()?.name ?: ""
                release_date = objSerie.body()?.release_date ?: ""
                rating = objSerie.body()?.rating?.toString() ?: ""
                category = objSerie.body()?.category ?: ""
            } catch (e: Exception) {
                Log.e("SERIE-EDITAR", "Error: ${e.message}")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = id.toString(),
            onValueChange = { },
            label = { Text("ID (solo lectura)") },
            readOnly = true,
            singleLine = true
        )
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name: ") },
            singleLine = true
        )
        TextField(
            value = release_date,
            onValueChange = { release_date = it },
            label = { Text("Release Date (AAAA-MM-DD):") },
            singleLine = true
        )
        TextField(
            value = rating,
            onValueChange = { rating = it },
            label = { Text("Rating:") },
            singleLine = true
        )
        TextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category:") },
            singleLine = true
        )
        Button(
            onClick = {
                scope.launch {
                    val objSerie = SerieModel(
                        id, name, release_date, rating.toIntOrNull() ?: 0, category
                    )
                    try {
                        if (id == 0)
                            servicio.insertSerie(objSerie)
                        else
                            servicio.updateSerie(id.toString(), objSerie)
                    } catch (e: Exception) {
                        Log.e("SERIE-GRABAR", "Error: ${e.message}")
                    }
                    navController.navigate("series")
                }
            }
        ) {
            Text("Grabar", fontSize = 16.sp)
        }
    }
}

@Composable
fun ContenidoSerieEliminar(navController: NavHostController, servicio: SerieApiService, id: Int) {
    var showDialog by remember { mutableStateOf(true) }
    var borrar by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                navController.navigate("series")
            },
            title = { Text(text = "Confirmar Eliminación") },
            text = { Text("¿Está seguro de eliminar la Serie?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        borrar = true
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        navController.navigate("series")
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (borrar) {
        LaunchedEffect(Unit) {
            try {
                servicio.deleteSerie(id.toString())
            } catch (e: Exception) {
                Log.e("SERIE-ELIMINAR", "Error: ${e.message}")
            }
            borrar = false
            navController.navigate("series")
        }
    }
}