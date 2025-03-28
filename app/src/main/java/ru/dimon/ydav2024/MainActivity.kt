package ru.dimon.ydav2024

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.os.Bundle
import android.text.Html
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference


class MainActivity : ComponentActivity() {
    @Preview
    @Composable
    private fun politic(){
        val html = stringResource(R.string.politic)
        val textPolitic = remember { Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT) }

        val boxVisible = remember { mutableStateOf(false) }
        Column( modifier=Modifier.padding(10.dp)) {
            Text(text = "Продолжая использование вы соглашаетесь с политикой конфиденциальности:",
            modifier = Modifier.clickable( onClick = {boxVisible.value=true}), textDecoration = TextDecoration.Underline)
            AnimatedVisibility(visible = boxVisible.value) {
                Column (modifier = Modifier.padding(10.dp).background(colorResource(R.color.fon))) {
                    Button({boxVisible.value=false}) { Text(text = "Согласен", fontSize = 24.sp) }
                    Text(
                        text = textPolitic.toString(),
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )

                }
            }
        }
    }
    /**
     * IP адрес устройства
     */
    private fun getDeviceIpAddress(context: Context?):String? {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val linkProperties: LinkProperties? = connectivityManager.getLinkProperties(connectivityManager.activeNetwork)
        if (linkProperties!=null) {
            return linkProperties.linkAddresses[1].address.hostAddress!!
        }
        return null
    }

   private fun startServiceMain(){
           val intent = Intent(
               WeakReference(this.applicationContext).get(),
               MainServerService::class.java
           )
           if (!MainServerService.isServiceCreated()) {

               val ipHost = getDeviceIpAddress(WeakReference(this).get())
               val mainServerService =Intent(WeakReference(this).get(), MainServerService::class.java)
               mainServerService.putExtra("Host", ipHost)
               ContextCompat.startForegroundService(
                   WeakReference(this).get()!!,
                   mainServerService
               )
               startForegroundService(intent)
           } else {
               stopService(intent)
           }
   }


   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var hostIp = getDeviceIpAddress(WeakReference(this).get())
        val arrayPermissionString = mutableMapOf(
            "android.permission.ANSWER_PHONE_CALLS" to " \"Список вызовов\"",
            "android.permission.ACCESS_COARSE_LOCATION" to " \"Местоположение\"",
            "android.permission.RECEIVE_SMS" to " \"СМС\"",
            "android.permission.READ_CONTACTS" to " \"Контакты\"",
            "android.permission.READ_PHONE_STATE" to " \"Телефон\"",
            "android.permission.SEND_SMS" to " \"Отправка СМС\"")
        val arrayPermission = mutableMapOf(
            "android.permission.ANSWER_PHONE_CALLS" to true,
            "android.permission.ACCESS_COARSE_LOCATION" to true,
            "android.permission.RECEIVE_SMS" to true,
            "android.permission.READ_CONTACTS" to true,
            "android.permission.READ_PHONE_STATE" to true,
            "android.permission.SEND_SMS" to true)
        for (permission in arrayPermission){
            val permissionStatus = ContextCompat.checkSelfPermission(this,permission.key)
            if (permissionStatus == PackageManager.PERMISSION_DENIED){
                permission.setValue(false)
            }
        }
        val permissionFalse = arrayPermission.filter { !it.value }
        if (permissionFalse.isEmpty()) {
            if (hostIp==null) {
                startServiceMain()
                hostIp = getDeviceIpAddress(WeakReference(this).get())
            }
            setContent {
                Column(modifier=Modifier.padding(horizontal = 5.dp, vertical = 20.dp)){
                    Text(
                     when (hostIp){
                        null , "" ->"Внимание, сервер остановлен! Ожидается включение WI-FI. Активны разрешения: "
                        else -> "Внимание, сервер $hostIp работает! Активны разрешения: "
                        }
                    )
                    for (permission in arrayPermission){
                        Row(modifier=Modifier.padding(horizontal = 10.dp)) {
                            Text(arrayPermissionString.getValue(permission.key), fontWeight = FontWeight.Bold)
                        }
                    }
                    politic()
                }
            }
        } else {
            val appName = getString(R.string.app_name)
            setContent {
                Column(modifier=Modifier.padding(horizontal = 5.dp)){
                    Text("Внимание, дальнейшая работа программы $appName невозможна, пока")
                    for (permission in permissionFalse){
                        Row(modifier=Modifier.padding(horizontal = 10.dp)) {                            Text("у Вас нет разрешения:")
                            Text(arrayPermissionString.getValue(permission.key), fontWeight = FontWeight.Bold)
                        }
                    }
                    Text("Для устранения проблемы дайте в настройках $appName выше перечисленные разрешения.")
                    Text("Перезапустите $appName.")
                    politic()
                }
            }

        }
    }
}
