package com.android.bledemo.utils

object AppConstant {
    //  Ble state
    const val BLE_STATE_CONNECT = "Connect"
    const val BLE_STATE_DISCONNECT = "Disconnect"
    const val BLE_STATE_CONNECTING = "Connecting"

    // Ble timer
    const val BLE_DESCRIPTOR_WRITE_DELAY = 2000L
    const val DEFAULT_SCAN_TIME_OUT = 60000L

    // Bundle data
    const val BUNDLE_BLE_DEVICE: String = "device"
    const val BUNDLE_FRAG_SERVICE_FROM = "service_fragment"
    const val BUNDLE_FROM_DEVICE_SCAN = "from_device_scan"

    var PREF_DEVICE_MAC_ADDRESS = ""
}