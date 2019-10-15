package com.android.bledemo.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import com.android.bledemo.ui.model.BLEDeviceModel
import com.android.bledemo.utils.AppConstant
import com.android.bledemo.utils.AppConstant.DEFAULT_SCAN_TIME_OUT
import com.android.bledemo.utils.AppConstant.PREF_DEVICE_MAC_ADDRESS
import java.util.*
import kotlin.collections.HashMap

class BLEManager {

    private var context: Context? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private val deviceList = ArrayList<BLEDeviceModel>()
    private val handler = Handler()
    val hashMap: HashMap<String, BLEDeviceModel>? = HashMap()
    private var bleAppInterface: BLEAppInterface? = null
    private var commandManager: BLECommandManager? = null
    private var myBluetoothGattCallback: BLEGattCallback? = null

    private val isBluetoothEnabled: Boolean
        get() = mBluetoothAdapter == null || !mBluetoothAdapter!!.isEnabled

    private val leScanCallback = BluetoothAdapter.LeScanCallback { device, _, _ ->
        if (!contains(device.address)) {
            deviceList.add(BLEDeviceModel(device))
            if (bleAppInterface != null) {
                bleAppInterface?.addDevice(BLEDeviceModel(device))
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Log.d(TAG, "onScanResult")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val device: BluetoothDevice = result.device
                if (!contains(device.address)) {
                    val bleDevice = BLEDeviceModel(device)
                    val manufacturerData = result.scanRecord?.manufacturerSpecificData
                    for (i in 0 until manufacturerData?.size()!!) {
                        val manufacturerId = manufacturerData.keyAt(i)
                        Log.d(TAG, "AANAL manufacturerId $manufacturerId")
                        bleDevice.manufacturerId = manufacturerId
                    }
                    deviceList.add(bleDevice)
                    if (bleAppInterface != null) {
                        if (device.address == PREF_DEVICE_MAC_ADDRESS)
                            bleAppInterface?.deviceFound(bleDevice)
                        else
                            bleAppInterface?.addDevice(bleDevice)
                    }
                }
            }
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                results[0].device
            }
            super.onBatchScanResults(results)
        }
    }

    private val stopScanningRunnable = Runnable { stopScanDevices() }

    private val isAutoConnect: Boolean
        get() = false

    fun init(context: Context, bleAppInterface: BLEAppInterface) {
        this.context = context

        enableBluetooth()
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter

        this.bleAppInterface = bleAppInterface
        commandManager = BLECommandManager(bleAppInterface, this)
        myBluetoothGattCallback = BLEGattCallback(bleAppInterface, commandManager!!, this)

    }

    /**
     *  This method use for start scan device
     *  @param interval: This param default value is DEFAULT_SCAN_TIME_OUT or pass value while call method
     *  @param connectedDevices: Connect device info. default value is null
     */
    fun startScanDevices(
        interval: Long = DEFAULT_SCAN_TIME_OUT,
        connectedDevices: HashMap<String, BLEDeviceModel>? = null
    ) {
        if (!isBluetoothEnabled) {
            clearDeviceList(connectedDevices)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //
                /*val scanFilter = ScanFilter.Builder()
                    .setServiceUuid(ParcelUuid(UUID.fromString(CommandConstant.BLE_SERVICE_UUID)))
                    .build()
                val scanFilters = ArrayList<ScanFilter>()
                scanFilters.add(scanFilter)

                val scanSettings = ScanSettings.Builder().build()

                mBluetoothAdapter?.bluetoothLeScanner?.startScan(scanFilters, scanSettings, scanCallback)*/
                mBluetoothAdapter?.bluetoothLeScanner?.startScan(scanCallback)
            } else {
                mBluetoothAdapter?.startLeScan(leScanCallback)
            }
            handler.postDelayed(stopScanningRunnable, interval)
        }
    }

    /**
     * This method use for stop ble device scanning
     */
    fun stopScanDevices() {
        if (!isBluetoothEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
            } else {
                mBluetoothAdapter?.stopLeScan(leScanCallback)
            }
        }
    }

    /**
     * This method use for connect BLE device or set up BLEGattCallback
     */
    fun connectDevice(bleDeviceModel: BLEDeviceModel?) {
        if (bleDeviceModel?.bleDevice != null) {
            hashMap!![bleDeviceModel.bleDevice.address] = bleDeviceModel
            val bleDevice = bleDeviceModel.bleDevice
            val bluetoothGatt: BluetoothGatt? =
                bleDevice.connectGatt(context, isAutoConnect, myBluetoothGattCallback)
            bleDeviceModel.bluetoothGatt = bluetoothGatt
            bleAppInterface?.connectionStatus(bleDeviceModel, ConnectionStatus.CONNECTING)
        }
    }

    fun removeDevice(macAddress: String) {
        hashMap?.remove(macAddress)
    }

    fun addDevice(macAddress: String) {
        PREF_DEVICE_MAC_ADDRESS = macAddress
    }

    /***
     * This method use for disconnect ble device connection
     */
    fun disconnect(bleDeviceModel: BLEDeviceModel?) {
        if (mBluetoothAdapter == null || bleDeviceModel!!.bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return
        }
        bleDeviceModel.bluetoothGatt?.disconnect()
    }

    private fun enableBluetooth() {
        BluetoothAdapter.getDefaultAdapter().enable()
    }

    /**
     * Clear temporary scan device list
     */
    private fun clearDeviceList(connectedDevices: HashMap<String, BLEDeviceModel>?) {
        deviceList.clear()
        bleAppInterface?.setDeviceList(deviceList)
        if (connectedDevices != null)
            for (device in connectedDevices.values) {
                // called when device is connected
                if (device.connectionState == AppConstant.BLE_STATE_DISCONNECT) {
                    deviceList.add(device)
                }
            }
    }

    /**
     * Use for check scan device is all ready in list or not
     * @return : true is list contain device otherwise false
     */
    private operator fun contains(macAdd: String): Boolean {
        for (item in deviceList) {
            if (item.bleDevice.address == macAdd) {
                return true
            }
        }
        return false
    }

    fun sendCommand(command: Byte, sensorDetails: Boolean = false, selectedMacAddress: String) {
        commandManager?.sendCommand(
            command,
            sensorDetails = sensorDetails,
            selectedMacAddress = selectedMacAddress
        )
    }

    companion object {
        private val TAG = BLEManager::class.java.simpleName
    }

    /**
     * Enum of Ble device different state
     */
    enum class ConnectionStatus(val status: String) {
        CONNECTED(AppConstant.BLE_STATE_DISCONNECT),
        DISCONNECTED(AppConstant.BLE_STATE_CONNECT),
        CONNECTING(AppConstant.BLE_STATE_CONNECTING),
    }
}
