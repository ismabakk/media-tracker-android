package edu.metrostate.ics342.mediatracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import edu.metrostate.ics342.mediatracker.data.network.RetrofitInstance
import edu.metrostate.ics342.mediatracker.navigation.MediaTrackerNavGraph

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RetrofitInstance.initialize(
            context = applicationContext
        )

        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    val navController = rememberNavController()

    MediaTrackerNavGraph(
        navController = navController
    )
}