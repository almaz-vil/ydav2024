package ru.dimon.ydav2024

import android.content.Intent
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.dimon.ydav2024.ui.theme.Ydav2024Theme


class MainActivity : ComponentActivity() {
    /**
     * IP адрес устройства
      */
    private fun getDeviceIpAddress(connectivityManager:ConnectivityManager):String? {
         val linkProperties:LinkProperties? = connectivityManager.getLinkProperties(connectivityManager.activeNetwork)
        if (linkProperties!=null) {
            return linkProperties.linkAddresses[1].address.hostAddress!!
        }
        return null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        var ipAdressHost = getDeviceIpAddress(connectivityManager)
        if (ipAdressHost != null) {
            val intent = Intent(this, MainServerService::class.java)
            intent.putExtra("Host", ipAdressHost)
            if (!MainServerService.isServiceCreated()) {
                Log.d("ydav", "start ip = $ipAdressHost")
                startForegroundService(intent)
            } else {
                stopService(intent)
            }
        } else {
            ipAdressHost="Ошибка. IP адрес устройства не найдено"
        }
        enableEdgeToEdge()
        setContent {
            Ydav2024Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = ipAdressHost,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Состояние сервера. $name",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Ydav2024Theme {
        Greeting("Android")
    }
}
