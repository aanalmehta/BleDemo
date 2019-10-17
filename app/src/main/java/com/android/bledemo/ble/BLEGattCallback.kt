package com.android.bledemo.ble

import android.bluetooth.*
import android.util.Log
import java.util.*


class BLEGattCallback(private val bleAppInterface: BLEAppInterface, private val commandManager: BLECommandManager, private val bleManager: BLEManager
) : BluetoothGattCallback() {

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        Log.d("AANAL", "AANAL => onConnectionStateChange")
        val bleDeviceModel = bleManager.hashMap!![gatt.device.address]
        if (status == BluetoothGatt.GATT_SUCCESS) {
            when (newState) {
                BluetoothGatt.STATE_CONNECTED -> {
                    bleDeviceModel?.bluetoothGatt?.discoverServices()
                    bleManager.addDevice(gatt.device.address)
                }
                BluetoothGatt.STATE_DISCONNECTED -> {
                    bleAppInterface.connectionStatus(bleDeviceModel!!,
                        BLEManager.ConnectionStatus.DISCONNECTED
                    )
                    bleDeviceModel.bluetoothGatt?.disconnect()
                    bleDeviceModel.bluetoothGatt?.close()
                    bleManager.removeDevice(gatt.device.address)

                }
                BluetoothGatt.STATE_CONNECTING -> {
                    bleAppInterface.connectionStatus(bleDeviceModel!!,
                        BLEManager.ConnectionStatus.CONNECTING
                    )
                }
            }
        } else {
            bleAppInterface.connectionStatus(bleDeviceModel!!,
                BLEManager.ConnectionStatus.DISCONNECTED
            )
            bleDeviceModel.bluetoothGatt?.disconnect()
            bleDeviceModel.bluetoothGatt?.close()
            bleManager.removeDevice(gatt.device.address)
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        super.onServicesDiscovered(gatt, status)
        Log.d("AANAL", "AANAL => onServicesDiscovered")
        val bleDeviceModel = bleManager.hashMap!![gatt.device.address]

        if (status == BluetoothGatt.GATT_SUCCESS) {
            val bleService: List<BluetoothGattService>? = bleDeviceModel?.bluetoothGatt?.services

            bleService?.map { it.characteristics }?.forEach { characteristicList ->
                characteristicList
                        .filter { bleDeviceModel.bluetoothGatt!!.setCharacteristicNotification(it, true) }
                        .forEach {
                            bleDeviceModel
                                    .hashMapGattCharacteristic[it.uuid.toString()] = it
                        }
            }
            if (bleService != null && bleService.isNotEmpty()) {
                bleAppInterface.foundServices(bleDeviceModel)
            }
            bleAppInterface.connectionStatus(
                bleDeviceModel!!,
                BLEManager.ConnectionStatus.CONNECTED
            )
        } else {
            bleAppInterface.connectionStatus(bleDeviceModel!!,
                BLEManager.ConnectionStatus.DISCONNECTED
            )
        }

    }

    override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
        super.onCharacteristicWrite(gatt, characteristic, status)
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d("AANAL", "onCharacteristicWrite")
        }
    }

    override fun onCharacteristicRead(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        super.onCharacteristicRead(gatt, characteristic, status)
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d("AANAL", "onCharacteristicRead")
            bleAppInterface.readCharacteristics(characteristic?.value)
        }
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
       super.onCharacteristicChanged(gatt, characteristic)
        Log.e("AANAL", "AANAL onCharacteristicChanged called ${Arrays.toString(characteristic.value)}")
        commandManager.parseResponse(characteristic.value)
    }

    override fun onDescriptorRead(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
        super.onDescriptorRead(gatt, descriptor, status)
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.e("AANAL", String(descriptor.value))
        }
    }
}

















































