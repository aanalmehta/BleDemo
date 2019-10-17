package com.android.bledemo.ble

import android.util.Log

class BLECommandManager(private val bleAppInterface: BLEAppInterface) {

    /**
     *  This function will call when onCharacteristicChanged will call
     *  @param b: this param contains byte array received from the BLE
     */
    fun parseResponse(b: ByteArray) {
        Log.d("AANAL", "AANAL => ${byteArrayToString(b)}")
        val byteString = b[1].toString() // Here we know that we will receive data in the second byte
        bleAppInterface.setCommandResult(byteString)
    }

    private fun byteArrayToString(byteArray: ByteArray): String {
        val stringBuilder = StringBuilder(byteArray.size)
        for (b in byteArray) {
            stringBuilder.append(String.format("%02X ", b))
        }
        return stringBuilder.toString()
    }
}
