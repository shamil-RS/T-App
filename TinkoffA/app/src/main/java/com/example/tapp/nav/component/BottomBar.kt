package com.example.tapp.nav.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.tapp.R
import com.example.tapp.nav.bottomNavigationBarItems

@Composable
fun MappaBottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    currentScreen: NavDestination?
) {
    BottomAppBar(
        modifier = modifier
            .height(70.dp)
            .fillMaxWidth(),
        elevation = 10.dp,
        backgroundColor = Color.Black,
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .selectableGroup(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = CenterVertically
        ) {
            bottomNavigationBarItems.forEach { listBottomBar ->
                BottomBarItem(
                    label = listBottomBar.title,
                    icon = listBottomBar.icon,
                    selected = currentScreen?.hierarchy?.any { navDestination ->
                        navDestination.route == listBottomBar.route
                    } == true,
                    onClick = {
                        navController.navigate(listBottomBar.route) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BottomBarItem(
    @DrawableRes icon: Int = R.drawable.card_payment,
    label: String = "Главная",
    onClick: () -> Unit = {},
    selected: Boolean = false,
) {

    val colorT = if (selected) Color(0xFF3b78d7) else Color(0xFF767e85)

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false),
                onClick = { onClick() }),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Icon(
            modifier = Modifier
                .align(CenterHorizontally)
                .size(22.dp),
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = colorT
        )
        Text(text = label, color = colorT, fontWeight = FontWeight.Bold)
    }
}