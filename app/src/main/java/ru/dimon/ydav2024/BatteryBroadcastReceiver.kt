package ru.dimon.ydav2024

/**
 * Получение информации о батареи устройства
 * отправка Intent сервису для сохранения полученной информации
 *         температура
 *         текущий уровень заряда
 *         максимальный уровень заряда
 *         состояние батареи
 *         тип/статус зарядки
 */
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager

class BatteryBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
            //температура
            val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1).toFloat() / 10
            //текущий уровень заряда
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1).toFloat()
            //состояние батареи
            val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
            val status = when (health) {
                BatteryManager.BATTERY_HEALTH_DEAD -> "батарея полностью неработоспособна"
                BatteryManager.BATTERY_HEALTH_GOOD -> "батарея в хорошем состоянии"
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "у батареи повышено напряжение"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "батарея перегрета"
                BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "батарея неисправна"
                BatteryManager.BATTERY_HEALTH_UNKNOWN -> "статус батареи неизвестен"
                else -> "информация недоступна"
            }
            //тип зарядки батареи
            val chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val charge = when (chargePlug) {
                BatteryManager.BATTERY_PLUGGED_USB -> "USB"
                BatteryManager.BATTERY_PLUGGED_AC -> "AC"
                else -> ""
            }
            val infoBattery = Intent(context.applicationContext, BatteryService::class.java)
            infoBattery.putExtra("Temperature", temperature)
            infoBattery.putExtra("Level", level)
            infoBattery.putExtra("Charge", charge)
            infoBattery.putExtra("Status", status)
            context.startService(infoBattery)
        }
    }
}