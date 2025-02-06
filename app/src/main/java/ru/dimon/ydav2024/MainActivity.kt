package ru.dimon.ydav2024

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference


class MainActivity : ComponentActivity() {

   private fun startServiceMain(){
           val intent = Intent(
               WeakReference(this.applicationContext).get(),
               MainServerService::class.java
           )
           if (!MainServerService.isServiceCreated()) {
               startForegroundService(intent)
           } else {
               stopService(intent)
           }
   }

   private fun getHost():String?{
       if (MainServerService.isServiceCreated()){
           Database.setContext(WeakReference(this.applicationContext).get()!!)
           val addressIp  = AddressIp(Database)
           return addressIp.getAddressIp()
       }
       return null
   }

   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var hostIp = getHost()
        val arrayPermissionString = mutableMapOf(
            "android.permission.ANSWER_PHONE_CALLS" to " \"Список вызовов\"",
            "android.permission.ACCESS_COARSE_LOCATION" to " \"Местоположение\"",
            "android.permission.RECEIVE_SMS" to " \"СМС\"",
            "android.permission.READ_CONTACTS" to " \"Контакты\"",
            "android.permission.READ_PHONE_STATE" to " \"Телефон\"")
        val arrayPermission = mutableMapOf(
            "android.permission.ANSWER_PHONE_CALLS" to true,
            "android.permission.ACCESS_COARSE_LOCATION" to true,
            "android.permission.RECEIVE_SMS" to true,
            "android.permission.READ_CONTACTS" to true,
            "android.permission.READ_PHONE_STATE" to true)
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
                hostIp = getHost()
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
                }
            }

        }
    }
}
