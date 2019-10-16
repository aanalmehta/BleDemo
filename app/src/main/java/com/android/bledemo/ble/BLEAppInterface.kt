package com.android.bledemo.ble

import com.android.bledemo.ui.model.BLEDeviceModel
import java.util.*


interface BLEAppInterface {

    fun addDevice(bleDeviceModel: BLEDeviceModel)

    fun setDeviceList(deviceList: ArrayList<BLEDeviceModel>)

    fun connectionStatus(bleDeviceModel: BLEDeviceModel, status: BLEManager.ConnectionStatus)

    fun foundServices(device: BLEDeviceModel)

    fun setCommandResult(value: String)

    fun deviceFound(device: BLEDeviceModel)

    fun readCharacteristics(value: ByteArray?)
}
