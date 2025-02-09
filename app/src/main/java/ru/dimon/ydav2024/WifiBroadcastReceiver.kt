package ru.dimon.ydav2024

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.wifi.WifiManager
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startForegroundService
import java.lang.ref.WeakReference

enum class ConnectStatus{
    NOT_CONNECTED, WIFI,
}

class WifiBroadcastReceiver: BroadcastReceiver() {

    private fun getConnectActivityStatus(context: Context?):ConnectStatus{
        val wifiManager = context?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifiManager.isWifiEnabled){
            return ConnectStatus.WIFI
        }
        return ConnectStatus.NOT_CONNECTED
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

    private fun isPermission(context: Context?):Boolean{
        val arrayPermission = mutableMapOf(
            "android.permission.ANSWER_PHONE_CALLS" to true,
            "android.permission.ACCESS_COARSE_LOCATION" to true,
            "android.permission.RECEIVE_SMS" to true,
            "android.permission.READ_CONTACTS" to true,
            "android.permission.READ_PHONE_STATE" to true)
        for (permission in arrayPermission){
            val permissionStatus = ContextCompat.checkSelfPermission(context!!,permission.key)
            if (permissionStatus == PackageManager.PERMISSION_DENIED){
                permission.setValue(false)
            }
        }
        val permissionFalse = arrayPermission.filter { !it.value }
        return permissionFalse.isEmpty()
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action=="android.net.wifi.WIFI_STATE_CHANGED") {
            val status = getConnectActivityStatus(WeakReference(context).get())
            if ((status == ConnectStatus.WIFI) && isPermission(WeakReference(context).get())) {
                var ipHost = getDeviceIpAddress(WeakReference(context).get())
                val mainServerService =
                    Intent(WeakReference(context).get(), MainServerService::class.java)
                while (ipHost == null) {
                    ipHost = getDeviceIpAddress(WeakReference(context).get())
                }
                mainServerService.putExtra("Host", ipHost)

                startForegroundService(WeakReference(context).get()!!, mainServerService)

            }
        }
    }

}