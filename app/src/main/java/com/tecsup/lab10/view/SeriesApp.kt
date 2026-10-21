package com.tecsup.lab10.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.lab10.data.ProductoApiService
import com.tecsup.lab10.data.SerieApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun SeriesApp() {
    val urlBase = "http://10.0.2.2:8000/" // o tu IP si usarás un dispositivo externo
    val retrofit = Retrofit.Builder().baseUrl(urlBase)
        .addConverterFactory(GsonConverterFactory.create()).build()
    val servicio = retrofit.create(SerieApiService::class.java)
    val servicioProd = retrofit.create(ProductoApiService::class.java)
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.padding(top = 40.dp),
        topBar = { BarraSuperior() },
        bottomBar = { BarraInferior(navController) },
        floatingActionButton = { BotonFAB(navController) },
        content = { paddingValues ->
            Contenido(paddingValues, navController, servicio, servicioProd)
        }
    )
}

@Composable
fun BotonFAB(navController: NavHostController) {
    val cbeState by navController.currentBackStackEntryAsState()
    val rutaActual = cbeState?.destination?.route
    if (rutaActual == "series" || rutaActual == "productos") {
        FloatingActionButton(
            containerColor = Color.Magenta,
            contentColor = Color.White,
            onClick = {
                if (rutaActual == "series")
                    navController.navigate("serieNuevo")
                else
                    navController.navigate("productoNuevo")
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "SERIES APP",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun BarraInferior(navController: NavHostController) {
    val cbeState by navController.currentBackStackEntryAsState()
    val rutaActual = cbeState?.destination?.route

    NavigationBar(
        containerColor = Color.LightGray
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = rutaActual == "inicio",
            onClick = { navController.navigate("inicio") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Favorite, contentDescription = "Series") },
            label = { Text("Series") },
            selected = rutaActual == "series",
            onClick = { navController.navigate("series") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.ShoppingCart, contentDescription = "Productos") },
            label = { Text("Productos") },
            selected = rutaActual == "productos",
            onClick = { navController.navigate("productos") }
        )
    }
}

@Composable
fun Contenido(
    pv: PaddingValues,
    navController: NavHostController,
    servicio: SerieApiService,
    servicioProd: ProductoApiService
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(pv)
    ) {
        NavHost(
            navController = navController,
            startDestination = "inicio"
        ) {
            composable("inicio") { ScreenInicio() }

            // Series
            composable("series") { ContenidoSeriesListado(navController, servicio) }
            composable("serieNuevo") {
                ContenidoSerieEditar(navController, servicio, 0)
            }
            composable(
                "serieVer/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                ContenidoSerieEditar(navController, servicio, it.arguments!!.getInt("id"))
            }
            composable(
                "serieDel/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                ContenidoSerieEliminar(navController, servicio, it.arguments!!.getInt("id"))
            }

            // Productos
            composable("productos") { ContenidoProductosListado(navController, servicioProd) }
            composable("productoNuevo") {
                ContenidoProductoEditar(navController, servicioProd, 0)
            }
            composable(
                "productoVer/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                ContenidoProductoEditar(navController, servicioProd, it.arguments!!.getInt("id"))
            }
            composable(
                "productoDel/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                ContenidoProductoEliminar(navController, servicioProd, it.arguments!!.getInt("id"))
            }
        }
    }
}