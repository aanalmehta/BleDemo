package com.android.bledemo.utils

import android.content.Context
import android.location.LocationManager
import java.io.ByteArrayOutputStream

/**
 * Use for create command byte code based on selected command code
 */
fun createCommand(command: Byte): ByteArray {
    val boas = ByteArrayOutputStream()
    boas.write(command.toInt())
    if (command == CommandConstant.BLE_LOGGING) {
        val unixTime = (System.currentTimeMillis() / 1000).toInt()
        return byteArrayOf(
            boas.toByteArray()[0],
            (unixTime shr 24).toByte(),
            (unixTime shr 16).toByte(),
            (unixTime shr 8).toByte(),
            unixTime.toByte()
        )
    }
    return boas.toByteArray()
}

/**
 * Use for check location is enable or not.
 */
fun isLocationEnable(context: Context?): Boolean {
    if (context != null) {
        val locationManager = context
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    return false
}