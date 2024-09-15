package com.example.tapp.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tapp.HomeScreen
import com.example.tapp.StoryViewer

@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = HomeBottomBarNavigation.HomeScreen1.route) {
        composable(route = HomeBottomBarNavigation.HomeScreen1.route) { HomeScreen(navController) }
        composable("storyViewer/{storyId}") { backStackEntry ->
            val storyId = backStackEntry.arguments?.getString("storyId")?.toInt() ?: 0
            StoryViewer(storyId = storyId, navController =  navController)
        }

        composable(route = HomeBottomBarNavigation.TicketsScreen.route) {  }
        composable(route = HomeBottomBarNavigation.SearchesScreen.route) {  }
        composable(route = HomeBottomBarNavigation.BookmarksScreen.route) {  }
        composable(route = HomeBottomBarNavigation.UserScreen.route) {  }
    }
}
