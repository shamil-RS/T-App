package com.example.tapp.nav

import androidx.annotation.DrawableRes
import com.example.tapp.R

const val TICKETS_SCREEN = "tickets"
const val BOOKMARKS_SCREEN = "bookmarks"
const val SEARCH_SCREEN = "search"
const val USER_SCREEN = "account"

sealed class HomeBottomBarNavigation(
    val title: String,
    val route: String,
    @DrawableRes val icon: Int
) {
    data object HomeScreen1 : HomeBottomBarNavigation(
        title = "Главная",
        route = "home_screen",
        icon = R.drawable.card_payment
    )

    data object TicketsScreen : HomeBottomBarNavigation(
        title = "Платежи",
        route = TICKETS_SCREEN,
        icon = R.drawable.points
    )

    data object BookmarksScreen : HomeBottomBarNavigation(
        title = "Город",
        route = BOOKMARKS_SCREEN,
        icon = R.drawable.direction
    )

    data object SearchesScreen : HomeBottomBarNavigation(
        title = "Чат",
        route = SEARCH_SCREEN,
        icon = R.drawable.comment
    )

    data object UserScreen : HomeBottomBarNavigation(
        title = "Еще",
        route = USER_SCREEN,
        icon = R.drawable.application,
    )
}

val bottomNavigationBarItems = listOf(
    HomeBottomBarNavigation.HomeScreen1,
    HomeBottomBarNavigation.TicketsScreen,
    HomeBottomBarNavigation.BookmarksScreen,
    HomeBottomBarNavigation.SearchesScreen,
    HomeBottomBarNavigation.UserScreen
)