package com.android.bledemo.utils

import android.bluetooth.BluetoothGattCharacteristic

object CommandConstant {
    var GENERIC_SERVICE_UUID = "00001800-0000-1000-8000-00805f9b34fb"// Generic information of device like DeviceName
    var DEVICE_NAME_CHARACTERISTIC_UUID = "00002a00-0000-1000-8000-00805f9b34fb"
    var APPEARANCE_CHARACTERISTIC_UUID = "00002a01-0000-1000-8000-00805f9b34fb"
    var CONNECTION_PARAMETERS_CHARACTERISTIC_UUID = "00002a04-0000-1000-8000-00805f9b34fb"
    var BLE_GENERIC_ATTRIBUTE_SERVICE_UUID = "00001801-0000-1000-8000-00805f9b34fb"
    var SERVICE_CHANGED_CHARACTERISTIC_UUID = "00002a05-0000-1000-8000-00805f9b34fb"
    var HEART_RATE_SERVICE_UUID = "0000180d-0000-1000-8000-00805f9b34fb"
    var HEART_RATE_CHARACTERISTIC_UUID = "00002a37-0000-1000-8000-00805f9b34fb"
    var BODY_SENSOR_LOCATION_CHARACTERISTIC_UUID = "00002a38-0000-1000-8000-00805f9b34fb"
    var BATTERY_SERVICE_UUID = "0000180f-0000-1000-8000-00805f9b34fb"
    var BATTERY_LEVEL_CHARACTERISTIC_UUID = "00002a19-0000-1000-8000-00805f9b34fb"
    var DEVICE_INFORMATION_SERVICE_UUID = "0000180a-0000-1000-8000-00805f9b34fb"
    var MANUFACTURER_NAME_CHARACTERISTIC_UUID = "00002a29-0000-1000-8000-00805f9b34fb"
    var MODEL_CHARACTERISTIC_UUID = "00002a24-0000-1000-8000-00805f9b34fb"
    var HARDWARE_CHARACTERISTIC_UUID = "00002a27-0000-1000-8000-00805f9b34fb"
    var FIRMWARE_CHARACTERISTIC_UUID = "00002a26-0000-1000-8000-00805f9b34fb"
    var SOFTWARE_CHARACTERISTIC_UUID = "00002a28-0000-1000-8000-00805f9b34fb"
    var SYSTEM_ID_CHARACTERISTIC_UUID = "00002a23-0000-1000-8000-00805f9b34fb"

    fun findUUID(uuid: String): String {
        when (uuid) {
            GENERIC_SERVICE_UUID -> return "Generic Service"
            DEVICE_NAME_CHARACTERISTIC_UUID -> return "Device Name"
            APPEARANCE_CHARACTERISTIC_UUID -> return "Appearance"
            CONNECTION_PARAMETERS_CHARACTERISTIC_UUID -> return "Connection Parameters"
            BLE_GENERIC_ATTRIBUTE_SERVICE_UUID -> return "Generic Attribute"
            SERVICE_CHANGED_CHARACTERISTIC_UUID -> return "Service Changed"
            HEART_RATE_SERVICE_UUID -> return "Heart Rate Service"
            HEART_RATE_CHARACTERISTIC_UUID -> return "Heart Rate Data"
            BODY_SENSOR_LOCATION_CHARACTERISTIC_UUID -> return "Body Sensor Location"
            BATTERY_SERVICE_UUID -> return "Battery Service"
            BATTERY_LEVEL_CHARACTERISTIC_UUID -> return "Battery Level"
            DEVICE_INFORMATION_SERVICE_UUID -> return "Device Information Service"
            MANUFACTURER_NAME_CHARACTERISTIC_UUID -> return "Manufacturer Name"
            MODEL_CHARACTERISTIC_UUID -> return "Model"
            HARDWARE_CHARACTERISTIC_UUID -> return "Hardware"
            FIRMWARE_CHARACTERISTIC_UUID -> return "Firmware"
            SOFTWARE_CHARACTERISTIC_UUID -> return "Software"
            SYSTEM_ID_CHARACTERISTIC_UUID -> return "System ID"
        }
        return "Unknown"
    }

    fun getCharacteristicProperty(propertyValue: Int): String {
        when (propertyValue) {
            BluetoothGattCharacteristic.PROPERTY_INDICATE -> return "INDICATE"
            BluetoothGattCharacteristic.PROPERTY_NOTIFY -> return "NOTIFY"
            BluetoothGattCharacteristic.PROPERTY_READ -> return "READ"
            BluetoothGattCharacteristic.PROPERTY_WRITE -> return "WRITE"
            BluetoothGattCharacteristic.PROPERTY_READ + BluetoothGattCharacteristic.PROPERTY_WRITE -> return "READ WRITE"
            BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE -> return "WRITE NO RESPONSE"

        }
        return "UNKNOWN"
    }
}