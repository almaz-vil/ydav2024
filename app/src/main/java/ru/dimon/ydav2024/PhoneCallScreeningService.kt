package ru.dimon.ydav2024

/**
 * Для работы нужно установить приложение по умолчанию как АОН
 */
import android.telecom.Call.Details
import android.telecom.CallScreeningService
import java.lang.ref.WeakReference


class PhoneCallScreeningService: CallScreeningService() {

    override fun onScreenCall(details: Details) {
        val  phone = details.handle.schemeSpecificPart as String
        Database.setContext(WeakReference(this@PhoneCallScreeningService).get()!!)
        val phoneStatus = PhoneStatus(Database)
        phoneStatus.write(phone = phone)
    }

}