package ru.dimon.ydav2024

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.util.Log

class Batter: BroadcastReceiver() {
    override fun onReceive(con: Context, intent: Intent) {
        //температура
        val temperatyr =
            intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1).toFloat() / 10
        //текущий уровень зарядак
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        //максимальный уровень зарядак
        val maxlevel = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        //состояние батареи
        val healt = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
        var shealth = "нет информации"
        when (healt) {
            BatteryManager.BATTERY_HEALTH_DEAD -> shealth =
                "батарея полностью неработоспособна"

            BatteryManager.BATTERY_HEALTH_GOOD -> shealth = "батарея в хорошем состоянии"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> shealth =
                "у батареи повышено напряжение"

            BatteryManager.BATTERY_HEALTH_OVERHEAT -> shealth = "батерея перегрета"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> shealth =
                "батарея неисправна"

            BatteryManager.BATTERY_HEALTH_UNKNOWN -> shealth = "статус батареи неизвестен"
        }
        Log.d("myLogs", "bat $shealth $temperatyr $level $maxlevel $shealth")

        val servicINFOBattar = Intent(
            con,
            ServiceINFOBattery::class.java
        )
        servicINFOBattar.putExtra("Temper", temperatyr.toString())
        servicINFOBattar.putExtra("Lavel", level.toString())
        servicINFOBattar.putExtra("MaxLavel", maxlevel.toString())
        servicINFOBattar.putExtra("Status", shealth)
        con.startService(servicINFOBattar)
    }
}