package ru.dimon.ydav2024

/**
 * Для работы нужно установить приложение по умолчанию как АОН
 */
import android.telecom.Call.Details
import android.telecom.CallScreeningService


class PhoneCallScreeningService: CallScreeningService() {

    override fun onScreenCall(details: Details) {
        val  phoneNomer = details.handle.schemeSpecificPart as String
        val phoneStatus = PhoneStatus(this@PhoneCallScreeningService)
        phoneStatus.write(phone = phoneNomer)
    }
}