package com.android.bledemo.ble

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.android.bledemo.R
import com.android.bledemo.App
import com.android.bledemo.ui.model.BLECommandModel
import com.android.bledemo.ui.model.BLEDeviceModel
import com.android.bledemo.utils.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.HashMap

class BLECommandManager(private val bleAppInterface: BLEAppInterface, private val bleManager: BLEManager) {

    private var selectedCommand: Byte? = null
    private var connectedDevices: HashMap<String, Int> = hashMapOf()
    private val queue = ConcurrentLinkedQueue<BLECommandModel>()
    private var handler: Handler? = null
    private var deviceListIndex: HashMap<String, Int> = hashMapOf()

    init {
        handler = Handler(Looper.getMainLooper())
    }

    /**
     *  This function will call when onCharacteristicChanged will call
     *  @param b: this param contains byte array received from the BLE
     */
    fun parseResponse(b: ByteArray) {
        decodeRandomCommand(b)
    }

    /**
     *  This function is used to set sent command details to variables
     *  @param command: This param contains byte containing sent command
     *  @param sensorDetails: This param is true only when device list command is sent
     */
    private fun setCurrentCommand(command: Byte, sensorDetails: Boolean) {
        selectedCommand = command
        deviceListIndex = hashMapOf()
        if (sensorDetails)
            connectedDevices = hashMapOf()
    }

    /**
     * This function is write data in hub device
     */
    private fun writeCommand(command: Byte, device: BLEDeviceModel, characteristicType: CharacteristicType) {
        queue.offer(BLECommandModel(
            createCommand(command),
            characteristicType
        ))
        if (!queue.isEmpty()) {
            val bleCommandModel = queue.poll()
            if (bleCommandModel != null) {
                device.hashMapGattCharacteristic[device.selectedCharacteristic]!!.value = bleCommandModel.bytes
                when (bleCommandModel.characteristicType) {
                    CharacteristicType.READ -> {
                    }
                    CharacteristicType.WRITE ->
                        device.bluetoothGatt!!.writeCharacteristic(device.hashMapGattCharacteristic[device.selectedCharacteristic])
                }
            }
        }
    }

    private fun checkServiceCharacteristic(device: BLEDeviceModel): Boolean {
        if (device.selectedService.isEmpty()) {
            ToastUtils.longToast(App.instance!!.getString(R.string.error_select_service, device.bleDevice.name, device.bleDevice.address))
            return false
        } else if (device.selectedCharacteristic.isEmpty()) {
            ToastUtils.longToast(App.instance!!.getString(R.string.error_select_characteristics, device.bleDevice.name, device.bleDevice.address))
            return false
        }
        return true
    }

    fun sendCommand(command: Byte, characteristicType: CharacteristicType = CharacteristicType.WRITE, sensorDetails: Boolean, selectedMacAddress: String) {

        /*
        * if selectedMacAddress is empty, then all option is selected in bottom sheet
        * so we have to send command to all connected devices
        * */
        if (selectedMacAddress == "") {
            bleManager.hashMap!!.values.filter { checkServiceCharacteristic(it) }
                    .forEach {
                        writeCommand(command, it, characteristicType)
                    }
        } else {// send command to only selected mac address in bottom sheet

            val device = bleManager.hashMap!![selectedMacAddress]
            if (checkServiceCharacteristic(device!!)) {
                writeCommand(command, device, characteristicType)
            }
        }
        setCurrentCommand(command, sensorDetails)
    }

    private fun decodeRandomCommand(b: ByteArray) {
        Log.d("AANAL", "AANAL => ${byteArrayToString(b)}")
        val byteString = b[1].toString()
        bleAppInterface.setCommandResult(byteString)
    }

    private fun byteArrayToString(byteArray: ByteArray): String {
        val stringBuilder = StringBuilder(byteArray.size)
        for (b in byteArray) {
            stringBuilder.append(String.format("%02X ", b))
        }
        return stringBuilder.toString()
    }

    enum class CharacteristicType {
        READ,
        WRITE
    }
}
