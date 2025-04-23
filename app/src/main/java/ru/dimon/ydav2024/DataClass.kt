package ru.dimon.ydav2024

import kotlinx.serialization.Serializable

@Serializable
data class Info(val time:String,
    val battery: BatteryData,
    val signal: MyCellInfoLteData,
    val sms: Int,
    val phone: Int)

@Serializable
data class Phone(val time: String,
                 val phone: List<Phones>)
@Serializable
data class DeletePhone(val time: String,
                       val phone: Int)
@Serializable
data class DeleteSmsInput(val time: String,
                          val sms: Int)
@Serializable
data class SmsOutputD(val time: String,
                      val status: SmsOutputData)
@Serializable
data class SmsInputD(val time: String,
                     val sms: List<SmsData>)
@Serializable
data class UssdSend(val time: String,
                    val ussd: UssdData)
@Serializable
data class ContactD(val time: String,
                    val contact: List<ContactData>)
@Serializable
data class HostD(val time: String,
                 val ipHost: String)
