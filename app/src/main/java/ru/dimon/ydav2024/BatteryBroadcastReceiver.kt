package ru.dimon.ydav2024

/**
 * Получение информации о батареи устройства
 * отправка Intent сервису для сохранения полученной информации
 *         температура
 *         текущий уровень заряда
 *         максимальный уровень заряда
 *         состояние батареи
 */
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager

class BatteryBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        //температура
        val temperatyr = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1).toFloat() / 10
        //текущий уровень заряда
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1).toFloat()
        //максимальный уровень заряда
        val maxlevel = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1).toFloat()
        //состояние батареи
        val healt = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
        val shealth = when (healt) {
            BatteryManager.BATTERY_HEALTH_DEAD -> "батарея полностью неработоспособна"
            BatteryManager.BATTERY_HEALTH_GOOD -> "батарея в хорошем состоянии"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "у батареи повышено напряжение"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "батерея перегрета"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "батарея неисправна"
            BatteryManager.BATTERY_HEALTH_UNKNOWN -> "статус батареи неизвестен"
            else -> "информация недоступна"
        }
        val servicINFOBattar = Intent( context, BatteryService::class.java)
        servicINFOBattar.putExtra("Temper", temperatyr)
        servicINFOBattar.putExtra("Lavel", level)
        servicINFOBattar.putExtra("MaxLavel", maxlevel)
        servicINFOBattar.putExtra("Status", shealth)
        context.startService(servicINFOBattar)
    }
}