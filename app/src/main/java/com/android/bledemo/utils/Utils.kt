package com.android.bledemo.utils

import android.content.Context
import android.location.LocationManager
import java.io.ByteArrayOutputStream

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