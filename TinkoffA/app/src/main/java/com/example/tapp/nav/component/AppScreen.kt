package com.example.tapp.nav.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tapp.nav.AppNavGraph
import com.example.tapp.nav.bottomNavigationBarItems

@Composable
fun AppScreen() {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentScreen = currentBackStack?.destination
    var bottomBarIsShow by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = currentScreen) {
        if (currentScreen != null && bottomBarIsShow != bottomNavigationBarItems.any { (it.route == currentScreen.route) }) {
            bottomBarIsShow = !bottomBarIsShow
        }
    }

    Scaffold(
        bottomBar = {
            if (bottomBarIsShow) {
                MappaBottomBar(
                    navController = navController,
                    modifier = Modifier,
                    currentScreen = currentScreen
                )
            }
        },
    ) { innerPadding ->
        AppNavGraph(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}