package com.example.moviedbapplication.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MainScreen(navController: NavController) {
    Scaffold { padding ->
        Button(
            onClick = { navController.navigate("details") },
            modifier = Modifier.padding(padding).padding(16.dp)
        ) {
            Text("Go to Details Screen")
        }
    }
}
